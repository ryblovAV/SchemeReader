package schemereader.json.reader

import java.io.{File, FileWriter, BufferedWriter}

import com.sun.corba.se.impl.orb.ORBConfiguratorImpl.ConfigParser
import schemereader.models.DBTable

import scala.io.Source

object DBTableReaderWriter {

  val path = "src/main/resources/data/DBScheme.json"

  def readScheme = {
    val bufferedSource = Source.fromFile(path)
    val jsonStr = bufferedSource.getLines.mkString("")
    bufferedSource.close
    jsonStr
  }

  def writeSceme(dbTables: List[DBTable]) = {
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(DBTableParser.jsonToStr(dbTables))
    bw.close()
  }

}
