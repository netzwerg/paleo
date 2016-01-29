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

import java.io.File
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}
import javaslang.control.Option

import ch.netzwerg.paleo.ColumnIds._
import ch.netzwerg.paleo._
import ch.netzwerg.paleo.schema.{Field, Schema}

import scala.collection.JavaConverters._
import scala.io.Source

object ScalaParserImpl {

  def parseTabDelimited(schema: Schema, parentDir: File): DataFrame = {
    val fields = schema.getFields.toJavaList.asScala
    val accumulators = fields.map(createAcc)
    for (lines <- Source.fromFile(new File(parentDir, schema.getDataFileName)).getLines()) {
      val values = lines.split("\t")
      accumulators.zip(values).map(t => t._1.addValue(t._2))
    }
    val columns: java.lang.Iterable[_ <: Column[_]] = accumulators.map(_.build()).toIterable.asJava
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
    val function: java.util.function.Function[String, DateTimeFormatter] = new java.util.function.Function[String, DateTimeFormatter]() {
      override def apply(pattern: String): DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
    }
    val formatter: Option[DateTimeFormatter] = field.getFormat.map(function)
    val builder = TimestampColumn.builder(TimestampColumnId.of(field.getName))
    val parseLogic: (String) => Instant = (s) => {
      if (formatter.isDefined) {
        val dateTime: LocalDateTime = LocalDateTime.from(formatter.get.parse(s))
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
