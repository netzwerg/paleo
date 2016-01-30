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
import java.lang
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.Scanner
import java.util.regex.Pattern
import javaslang.collection
import javaslang.control.Option

import ch.netzwerg.paleo.ColumnIds._
import ch.netzwerg.paleo._
import ch.netzwerg.paleo.schema.{Field, Schema}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.io.Source

object ScalaParserImpl {

  val LineDelimiter = Pattern.compile("[\\r\\n]+")

  def parseTabDelimited(reader: Reader, timestampPattern: Option[String]): DataFrame = {
    val scanner: Scanner = new Scanner(reader)
    scanner.useDelimiter(LineDelimiter)
    val lines = scanner

    val columnNames = lines.next().split("\t")
    val columnTypes = lines.next().split("\t")

    val fields: collection.List[Field] = createFields(columnNames, columnTypes, timestampPattern)

    parseTabDelimited(fields, lines)

  }

  private def createFields(columnNames: Array[String], columnTypes: Array[String], timestampFormat: Option[String]) = {
    val fields: Array[Field] = for ((columnName, columnTypeDesc) <- columnNames.zip(columnTypes)) yield {
      val columnType = ColumnType.getByDescriptionOrDefault(columnTypeDesc, ColumnType.STRING)

      // TODO: Offer type-safe Field constructor
      val format = columnType match {
        case ColumnType.TIMESTAMP => if (timestampFormat.isDefined) timestampFormat.get() else null
        case _ => null
      }

      new Field(columnName, columnType, format, null)
    }
    val iterator: lang.Iterable[Field] = () => fields.iterator
    javaslang.collection.List.ofAll[Field](iterator)
  }

  def parseTabDelimited(schema: Schema, parentDir: File): DataFrame = {
    val fields = schema.getFields
    val lines = Source.fromFile(new File(parentDir, schema.getDataFileName)).getLines()
    parseTabDelimited(fields, lines)
  }

  def parseTabDelimited(fields: javaslang.collection.Seq[Field], lines: java.util.Iterator[String]): DataFrame = {
    val scalaFields = fields.toJavaList.asScala
    val accumulators = scalaFields.map(createAcc)

    var rowIndex = 1
    for (line <- lines) {
      val values = line.split("\t", -1) // empty values allowed

      if (values.size != accumulators.length) {
        val msg = s"Row '$rowIndex' contains '${values.size}' values (but should match column count '${accumulators.length}')"
        throw new scala.IllegalArgumentException(msg)
      }

      accumulators.zip(values).map(t => t._1.addValue(t._2))
      rowIndex += 1
    }
    val columns: lang.Iterable[_ <: Column[_]] = accumulators.map(_.build()).toIterable.asJava
    DataFrame.ofAll(columns)
  }

  private def createAcc(field: Field): Acc[_, _ <: Column[_]] = {
    val acc = field.getType match {
      case ColumnType.BOOLEAN => new Acc[java.lang.Boolean, BooleanColumn](BooleanColumn.builder(BooleanColumnId.of(field.getName)), (s) => java.lang.Boolean.parseBoolean(s))
      case ColumnType.CATEGORY => new Acc[java.lang.String, CategoryColumn](CategoryColumn.builder(CategoryColumnId.of(field.getName)), (s) => s)
      case ColumnType.DOUBLE => new Acc[java.lang.Double, DoubleColumn](DoubleColumn.builder(DoubleColumnId.of(field.getName)), (s) => s.toDouble)
      case ColumnType.INT => new Acc[java.lang.Integer, IntColumn](IntColumn.builder(IntColumnId.of(field.getName)), (s) => s.toInt)
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

  def putAllMetaData(metaData: javaslang.collection.Map[String, String]) = {
    builder.putAllMetaData(metaData)
    this
  }

  def build(): C = builder.build()

}