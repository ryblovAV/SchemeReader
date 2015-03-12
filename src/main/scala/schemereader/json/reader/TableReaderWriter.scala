package schemereader.json.reader

import java.io.{BufferedWriter, File, FileWriter}


import play.api.libs.json.{JsValue, Json}
import schemereader.models.{Table, DBTable}

import scala.io.Source

object TableReaderWriter {

  val pathDBTables = "src/main/resources/data/DBScheme.json"
  val pathTables = "src/main/resources/data/Scheme.json"

  def readScheme = {
    val bufferedSource = Source.fromFile(pathDBTables)
    val jsonStr:String = bufferedSource.getLines.mkString("")
    bufferedSource.close

    jsonToDBTable(jsonStr)
  }

  def jsonToDBTable(str: String): List[DBTable] =
    TableParser.strToJson(Json.parse(str))

  def writeJSONToFile(path: String, json: JsValue) = {
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(json.toString())
    bw.close()
  }

  def writeDBTables(dbTables: List[DBTable]) =
    writeJSONToFile(pathDBTables, TableParser.dbTablesToJson(dbTables))

  def writeTables(tables: List[Table]) =
    writeJSONToFile(pathTables, TableParser.tablesToJson(tables))

}
