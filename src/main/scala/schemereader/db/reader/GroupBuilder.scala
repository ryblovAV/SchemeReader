package schemereader.db.reader

import grizzled.slf4j.Logging
import schemereader.models._

import scala.util.matching.Regex

object GroupBuilder extends Logging {

  def filterTablesWithKey(tables: List[DBTable]): List[KeyTable] =
    tables.filter(
      (t) => (tables.exists((t2) => t2.name == s"${t.name}_K")) && (t.pkColumns.size == 1)).map(
        (t) => KeyTable(t.name, t.owner, t.pkColumns(0), getEmbeddableTables(t.name, tables))
      )

  def buildMapKeyByColumn(keyTables: List[KeyTable]): Map[String, KeyTable] =
    keyTables.groupBy(_.pkColumn.name).map(m => (m._1 -> m._2(0)))

  def buildMapKeyByName(keyTables: List[KeyTable]): Map[String, KeyTable] =
    keyTables.groupBy(_.name).map(m => (m._1 -> m._2(0)))

  //build map EmbeddableTableName -> PrimaryKeyColumnName
  def buildMapPKByEmbTable(keyTables: List[KeyTable]):Map[String,String] = {
    //Primary Key column name -> List embedded table name
    val l:List[(String,String)] =
      keyTables.map( //KeyTable -> (PrimaryKeyColumnName, List[EmbeddedTableName])
        k => (k.pkColumn.name,k.embeddableTables.map(_.name))).map( //List[EmbeddedTableName,PrimaryKeyColumnName]
          t => t._2.map((embeddedTableName) => (embeddedTableName,t._1))).flatten

    l.groupBy(_._1).map(t => (t._1 -> t._2(0)._2))
  }


  def isEmbeddable(p: Regex, tableName: String): Boolean =
    !p.findFirstIn(tableName).isEmpty

  def getEmbeddableTables(mTabName: String, tables: List[DBTable]): List[DBTable] = {
    val p = (s"^(${mTabName}_CHAR|${mTabName}_K|${mTabName}_ID|${mTabName}_NAME|${mTabName}_PHONE)" + "$").r
    tables.filter((t) => isEmbeddable(p, t.name))
  }

  def fillRelation(table: DBTable, mapKeyTablesByColumn: Map[String,KeyTable]): (List[RefManyToOne],List[Column]) = {

    def checkColumn(c: Column, pkColumn: Column): Boolean = {

      def checkType(c: Column, pkColumn: Column): Boolean =
        (c.dataType == pkColumn.dataType) && (c.dataLength == pkColumn.dataLength)

      def checkName(c: Column, pkColumn: Column): Boolean =
        ((c.name.startsWith(pkColumn.name)) || (c.name.endsWith(pkColumn.name)))

      checkType(c, pkColumn) && checkName(c, pkColumn)
    }

    def getRelation(c: Column, mapKeyTables: Map[String,KeyTable]):Option[KeyTable] =
      mapKeyTables.get(c.name) match {
        case None => mapKeyTables.values.find((t) => checkColumn(c,t.pkColumn))
        case Some(k) => Some(k)
      }

    def add(l: (List[RefManyToOne],List[Column]), c: Column): (List[RefManyToOne],List[Column]) =
      l match {
        case (keys,columns) =>
          getRelation(c, mapKeyTablesByColumn) match {
            case Some(keyTable) => (RefManyToOne(keyTable.pkColumn.name,keyTable.name)::keys,columns)
            case _ => (keys,c::columns)
          }
    }

    table.columns.foldLeft((List[RefManyToOne](),List[Column]()))(add)

  }

  def createTable(dbTable: DBTable,
                  mapKeyTablesByColumn: Map[String,KeyTable],
                  mapKeyTablesByName: Map[String,KeyTable]):Table = {
    val (manyToOne,columns) = fillRelation(dbTable, mapKeyTablesByColumn)

    val embeddableTables = mapKeyTablesByName.get(dbTable.name) match {
      case Some(keyTable) => keyTable.embeddableTables
      case _ => Nil
    }

    Table(name = dbTable.name,
          owner = dbTable.owner,
          embeddable = 0,
          columns = columns,
          pkColumns = dbTable.pkColumns,
          embeddedTables = embeddableTables,
          manyToOne = manyToOne,
          oneToMany = Nil
    )
  }

  def createEmbeddableTable(dbTable: DBTable, mapPKByEmbeddedTable: Map[String,String]) = {
    val pkColumnName = mapPKByEmbeddedTable(dbTable.name)
    Table(name = dbTable.name,
          owner = dbTable.owner,
          embeddable = 1,
          columns = dbTable.columns:::(dbTable.pkColumns.filter((c) => c.name != pkColumnName)).map((c)=>c.copy(pkPosition = None)),
          pkColumns = Nil,
          embeddedTableNames = Nil,
          manyToOne = Nil,
          oneToMany = Nil)
  }

  def fillOneToManyRelations(tableName: String, tables: List[Table]): List[RefOneToMany] = {
    tables.filter((t) => t.manyToOne.exists((r) => r.tableName == tableName)).map((t) => RefOneToMany(tableName,t.name))
  }

  def run(dbTables: List[DBTable]):List[Table] = {

    val keyTables: List[KeyTable] = filterTablesWithKey(dbTables)

    val mapKeyTablesByName: Map[String, KeyTable] = buildMapKeyByName(keyTables)

    val mapKeyTablesByColumn: Map[String, KeyTable] = buildMapKeyByColumn(keyTables)

    val mapEmbeddedTables: Map[String, _] = mapKeyTablesByColumn.values.map(_.embeddableTables).flatten.groupBy(_.name)

    val (dbTablesEmbd, dbTablesNotEmbd) = dbTables.partition((t) => mapEmbeddedTables.contains(t.name))

    val tables = dbTablesNotEmbd.map(createTable(_,mapKeyTablesByColumn,mapKeyTablesByName))
    //add oneToMany relations
    val tablesWithR =  tables.map((t) => t.copy(oneToMany = fillOneToManyRelations(t.name,tables)))

    //build embeddable tables list
    val embeddableTables = dbTablesEmbd.map((t) => createEmbeddableTable(t,buildMapPKByEmbTable(keyTables)))

    tablesWithR:::embeddableTables
  }

}
