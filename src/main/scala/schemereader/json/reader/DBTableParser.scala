package schemereader.json.reader

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import schemereader.models.{Column, DBTable}

object DBTableParser {

  implicit val columnWrites: Writes[Column] = (
      (JsPath \ "name").write[String] and
      (JsPath \ "dataType").write[String] and
      (JsPath \ "defaultValue").writeNullable[String] and
      (JsPath \ "dataLength").write[Int] and
      (JsPath \ "dataPrecision").writeNullable[Int] and
      (JsPath \ "dataScale").writeNullable[Int] and
      (JsPath \ "pkPosition").writeNullable[Int]
    )(unlift(Column.unapply))

  implicit val columnReads: Reads[Column] = (
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

  implicit val dbTableReads: Reads[DBTable] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "owner").read[String] and
    (JsPath \ "columns").read[List[Column]] and
    (JsPath \ "pkColumns").read[List[Column]]
    )(DBTable.apply _)

  def jsonToStr(dbTables: List[DBTable]) = {
    Json.toJson(dbTables).toString()
  }


}
