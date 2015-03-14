package schemereader.json.reader

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import schemereader.models._

object TableParser {

  implicit val columnWrites: Writes[Column] = (
      (JsPath \ "name").write[String] and
      (JsPath \ "dataType").write[String] and
      (JsPath \ "defaultValue").writeNullable[String] and
      (JsPath \ "dataLength").write[Int] and
      (JsPath \ "dataPrecision").writeNullable[Int] and
      (JsPath \ "dataScale").writeNullable[Int] and
      (JsPath \ "pkPosition").writeNullable[Int]
    )(unlift(Column.unapply))

  implicit val columnReaders: Reads[Column] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "dataType").read[String] and
    (JsPath \ "defaultValue").readNullable[String] and
    (JsPath \ "dataLength").read[Int] and
    (JsPath \ "dataPrecision").readNullable[Int] and
    (JsPath \ "dataScale").readNullable[Int] and
    (JsPath \ "pkPosition").readNullable[Int]
    )(Column.apply _)

  implicit val dbTableWriters: Writes[DBTable] = (
      (JsPath \ "name").write[String] and
      (JsPath \ "owner").write[String] and
      (JsPath \ "columns").write[List[Column]] and
      (JsPath \ "pkColumns").write[List[Column]]
    )(unlift(DBTable.unapply))

  implicit val dbTableReaders: Reads[DBTable] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "owner").read[String] and
    (JsPath \ "columns").read[List[Column]] and
    (JsPath \ "pkColumns").read[List[Column]]
    )(DBTable.apply _)

  //ManyToOne
  implicit val refManyToOneWriters: Writes[RefManyToOne] = (
    (JsPath \ "columnName").write[String] and
    (JsPath \ "tableName").write[String]
    )(unlift(RefManyToOne.unapply))

  implicit val refManyToOneReaders: Reads[RefManyToOne] = (
    (JsPath \ "columnName").read[String] and
      (JsPath \ "tableName").read[String]
    )(RefManyToOne.apply _)

  //OneToMany
  implicit val refOneToManyWriters: Writes[RefOneToMany] = (
    (JsPath \ "oneTableName").write[String] and
    (JsPath \ "manyTableName").write[String]
    )(unlift(RefOneToMany.unapply))

  implicit val refOneToManyReaders: Reads[RefOneToMany] = (
    (JsPath \ "oneTableName").read[String] and
    (JsPath \ "manyTableName").read[String]
    )(RefOneToMany.apply _)

  //Table
//  name: String,
//  owner: String,
//  columns: List[Column],
//  pkColumns: List[Column],
//  embeddableTables: List[DBTable],
//  manyToOne: List[RefManyToOne],
//  oneToMany: List[RefOneToMany]
  implicit val tableWriters: Writes[Table] = (
    (JsPath \ "name").write[String] and
    (JsPath \ "owner").write[String] and
    (JsPath \ "embeddable").write[Int] and
    (JsPath \ "columns").write[List[Column]] and
    (JsPath \ "pkColumns").write[List[Column]] and
    (JsPath \ "embeddedTables").write[List[String]] and
    (JsPath \ "manyToOne").write[List[RefManyToOne]] and
    (JsPath \ "oneToMany").write[List[RefOneToMany]]
    )(unlift(Table.unapply))

  implicit val tableReaders: Reads[Table] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "owner").read[String] and
    (JsPath \ "embeddable").read[Int] and
    (JsPath \ "columns").read[List[Column]] and
    (JsPath \ "pkColumns").read[List[Column]] and
    (JsPath \ "embeddedTables").read[List[String]] and
    (JsPath \ "manyToOne").read[List[RefManyToOne]] and
    (JsPath \ "oneToMany").read[List[RefOneToMany]]
    )(Table.apply _)

  def tablesToJson(tables: List[Table]) =
    Json.toJson(tables)

  def dbTablesToJson(dbTables: List[DBTable]) =
    Json.toJson(dbTables)

  def strToJson(json: JsValue) =
    json.validate[List[DBTable]] match {
      case s: JsSuccess[List[DBTable]] => {
        s.get
      }
      case e: JsError => {
        throw new Exception(JsError.toString)
      }
    }



}
