package schemereader.db.reader


import org.springframework.context.support.ClassPathXmlApplicationContext
import schemereader.db.jdbc.JDBCExtractorSafe._
import schemereader.db.jdbc.JdbcTemplatesUtl._
import schemereader.db.jdbc.{JDBCExtractorSafe, JDBCTemplatesUtlImpl}
import schemereader.db.sql.SQLBuilder._
import schemereader.models.{Column, Table}

import scala.collection.JavaConversions._
import scala.collection._

object DBReader {

  val ctx = new ClassPathXmlApplicationContext("application-context.xml")
  val jdbcReader = ctx.getBean(classOf[JDBCTemplatesUtlImpl])

  def getTables = jdbcReader.query(sqlMaster){
    (rs, rowNum) => Table(name = rs.getString("table_name"),
                          owner = rs.getString("referenced_owner"),
                          columns = List())
  }

  def getColumns(tableName: String) = {
    val m = mutable.Map("table_name" -> tableName)
    jdbcReader.queryWithParameters(sqlColumns,m){
      (rs,nn) => Column(name = (rs,"column_name"),
                        dataType = (rs,"data_type"),
                        defaultValue = (rs,"data_default"),
                        dataLength = (rs,"data_length"),
                        dataPrecision = (rs,"data_precision"),
                        dataScale = (rs,"data_scale"),
                        isPrimary = false)
    }
  }


}
