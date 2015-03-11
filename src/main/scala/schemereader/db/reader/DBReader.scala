package schemereader.db.reader


import org.springframework.context.support.ClassPathXmlApplicationContext
import schemereader.db.jdbc.JDBCExtractorSafe._
import schemereader.db.jdbc.JDBCTemplatesUtlImpl
import schemereader.db.jdbc.JdbcTemplatesUtl._
import schemereader.db.sql.SQLBuilder._
import schemereader.models.{DBTable, Column, Table}

import scala.collection.JavaConversions._
import scala.collection._
import scala.language.reflectiveCalls

object DBReader {

  val ctx = new ClassPathXmlApplicationContext("application-context.xml")
  val jdbcReader = ctx.getBean(classOf[JDBCTemplatesUtlImpl])

  def getTables = jdbcReader.query(sqlAllTables){
    (rs, rowNum) => DBTable(
      name = (rs,"table_name"),
      owner = (rs,"referenced_owner"),
      columns = Nil,
      pkColumns = Nil
    )
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
        pkPosition = (rs,"pk_position"))
    }
  }

  def getTablesWithColumns = {

    def createCopyWithColumns(table: DBTable): DBTable = {
      val (columns, pkColumns) = DBReader.getColumns(table.name).partition((c) => c.pkPosition.isEmpty)
      table.copy(columns = columns, pkColumns = pkColumns)
    }

    DBReader.getTables.map((t) => createCopyWithColumns(t))
  }

}
