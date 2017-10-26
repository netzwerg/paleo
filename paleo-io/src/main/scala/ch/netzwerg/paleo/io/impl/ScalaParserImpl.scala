/*
 * Copyright 2016 Rahel Lüthy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.netzwerg.paleo.io.impl

import java.io.{File, Reader}
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.Scanner
import java.util.regex.Pattern

import ch.netzwerg.paleo.ColumnIds._
import io.vavr.collection
import io.vavr.collection.{HashMap, Map}
import io.vavr.control.Option
import ch.netzwerg.paleo._
import ch.netzwerg.paleo.schema.{Field, Schema}

import scala.collection.JavaConverters._
import scala.io.Source

object ScalaParserImpl {

  val LineDelimiter: Pattern = Pattern.compile("[\\r\\n]+")

  private val TabSplitter = (line: String) => line.split("\t", -1) // allow empty values
  private val CommaSplitter = (line: String) => line.split(",", -1) // allow empty values

  // -- Tab Delimited

  def parseViaReaderTabDelimited(reader: Reader, timestampPattern: Option[String]): DataFrame = {
    parseViaReader(reader, timestampPattern, TabSplitter, TabSplitter, TabSplitter)
  }

  def parseViaSchemaTabDelimited(schema: Schema, parentDir: File): DataFrame = {
    parseViaSchema(schema, parentDir, TabSplitter)
  }

  def parseViaFieldsTabDelimited(fields: _root_.io.vavr.collection.Seq[Field], lines: java.util.Iterator[String], rowIndexOffset: Int, dataFrameMetaData: Map[String, String]): DataFrame = {
    parseViaFields(fields, lines, rowIndexOffset, dataFrameMetaData, TabSplitter)
  }

  // -- Comma Separated

  def parseViaReaderCommaSeparated(reader: Reader, timestampPattern: Option[String]): DataFrame = {
    parseViaReader(reader, timestampPattern, CommaSplitter, CommaSplitter, CommaSplitter)
  }

  def parseViaSchemaCommaSeparated(schema: Schema, parentDir: File): DataFrame = {
    parseViaSchema(schema, parentDir, CommaSplitter)
  }

  def parseViaFieldsCommaSeparated(fields: _root_.io.vavr.collection.Seq[Field], lines: java.util.Iterator[String], rowIndexOffset: Int, dataFrameMetaData: Map[String, String]): DataFrame = {
    parseViaFields(fields, lines, rowIndexOffset, dataFrameMetaData, CommaSplitter)
  }

  // -- Generic Column/Type/Value extraction

  private def parseViaReader(reader: Reader,
                     timestampPattern: Option[String],
                     columnNameExtractor: (String) => Array[String],
                     columnTypeExtractor: (String) => Array[String],
                     valueExtractor: (String) => Array[String]): DataFrame = {
    val scanner: Scanner = new Scanner(reader)
    scanner.useDelimiter(LineDelimiter)
    val lines = scanner

    val columnNames = columnNameExtractor(lines.next())
    val columnTypes = columnTypeExtractor(lines.next())

    val fields: collection.List[Field] = createFields(columnNames, columnTypes, timestampPattern)

    parseViaFields(fields, lines, 2, HashMap.empty(), valueExtractor)

  }

  private def createFields(columnNames: Array[String], columnTypes: Array[String], timestampFormat: Option[String]) = {
    val fields: Array[Field] = for ((columnName, columnTypeDesc) <- columnNames.zip(columnTypes)) yield {
      val columnType = ColumnType.getByDescriptionOrDefault(columnTypeDesc, ColumnType.STRING)

      columnType match {
        case ColumnType.TIMESTAMP => new Field(columnName, columnType, timestampFormat)
        case _ => new Field(columnName, columnType, Option.none())
      }

    }
    _root_.io.vavr.collection.List.ofAll[Field](fields.toIterable.asJava)
  }

  private def parseViaSchema(schema: Schema, parentDir: File, valueExtractor: (String) => Array[String]): DataFrame = {
    val fields = schema.getFields
    val source = Source.fromFile(new File(parentDir, schema.getDataFileName))
    try {
      val lines = source.getLines()
      parseViaFields(fields, lines.asJava, 0, schema.getMetaData, valueExtractor)
    } finally {
      source.close()
    }
  }

  private def parseViaFields(fields: _root_.io.vavr.collection.Seq[Field], lines: java.util.Iterator[String], rowIndexOffset: Int, dataFrameMetaData: Map[String, String], valueExtractor: (String) => Array[String]): DataFrame = {
    val scalaFields = fields.toJavaList.asScala
    val accumulators = scalaFields.map(createAcc)

    var rowIndex = 1
    for (line <- lines.asScala) {
      val values = valueExtractor(line)

      if (values.length != accumulators.length) {
        val rowIndexForHumans = rowIndex + rowIndexOffset
        val plural = if (values.length > 1) "s" else ""
        val msg = s"Row '$rowIndexForHumans' contains '${values.length}' value$plural (but should match column count '${accumulators.length}')"
        throw new scala.IllegalArgumentException(msg)
      }

      accumulators.zip(values).map(t => t._1.addValue(t._2))
      rowIndex += 1
    }
    val columns = accumulators.map(_.build()).asJava
    DataFrame.ofAll(columns).withMetaData(dataFrameMetaData)
  }

  private def createAcc(field: Field): Acc[_, _ <: Column[_]] = {
    val acc = field.getType match {
      case ColumnType.BOOLEAN => new Acc[java.lang.Boolean, BooleanColumn](BooleanColumn.builder(BooleanColumnId.of(field.getName)), (s) => java.lang.Boolean.parseBoolean(s))
      case ColumnType.CATEGORY => new Acc[java.lang.String, CategoryColumn](CategoryColumn.builder(CategoryColumnId.of(field.getName)), (s) => s)
      case ColumnType.DOUBLE => new Acc[java.lang.Double, DoubleColumn](DoubleColumn.builder(DoubleColumnId.of(field.getName)), (s) => s.toDouble)
      case ColumnType.INT => new Acc[java.lang.Integer, IntColumn](IntColumn.builder(IntColumnId.of(field.getName)), (s) => s.toInt)
      case ColumnType.LONG => new Acc[java.lang.Long, LongColumn](LongColumn.builder(LongColumnId.of(field.getName)), (s) => s.toLong)
      case ColumnType.TIMESTAMP => createTimestampAcc(field)
      case _ => new Acc(StringColumn.builder(StringColumnId.of(field.getName)), (s) => s)
    }
    acc.putAllMetaData(field.getMetaData)
  }

  private def createTimestampAcc(field: Field): Acc[Instant, TimestampColumn] = {
    val formatter: Option[DateTimeFormatter] = field.getFormat.map((pattern: String) => DateTimeFormatter.ofPattern(pattern))
    val builder = TimestampColumn.builder(TimestampColumnId.of(field.getName))
    val parseLogic: (String) => Instant = s => {
      if (formatter.isDefined) {
        val dateTime = LocalDateTime.from(formatter.get().parse(s))
        dateTime.atZone(ZoneId.systemDefault).toInstant
      } else {
        Instant.parse(s)
      }
    }
    new Acc[Instant, TimestampColumn](builder, parseLogic)
  }

}

/**
  * Accumulates values by delegating to type-specific builders. The given 'parseLogic' abstracts the conversion from
  * textual to type-specific values.
  */
class Acc[V, C <: Column[_]](builder: Column.Builder[V, C], parseLogic: (String) => (V)) {

  def addValue(stringValue: String): Acc[V, C] = {
    builder.add(parseLogic.apply(stringValue))
    this
  }

  def putAllMetaData(metaData: _root_.io.vavr.collection.Map[String, String]): Acc[V, C] = {
    builder.putAllMetaData(metaData)
    this
  }

  def build(): C = builder.build()

}