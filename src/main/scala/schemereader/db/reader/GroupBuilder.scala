package schemereader.db.reader

import grizzled.slf4j.Logging
import schemereader.models._

import scala.util.matching.Regex

object GroupBuilder extends Logging {

  case class KeyTable(name: String, owner: String, pkColumn: Column, embeddableTables: List[DBTable])

  def filterTablesWithKey(tables: List[DBTable]): List[KeyTable] =
    tables.filter(
      (t) => (tables.exists((t2) => t2.name == s"${t.name}_K")) && (t.pkColumns.size == 1)).map(
        (t) => KeyTable(t.name, t.owner, t.pkColumns(0), getEmbeddableTables(t.name, tables))
      )

  def buildMapKey(keyTables: List[KeyTable]): Map[String, KeyTable] =
    keyTables.groupBy(_.pkColumn.name).map(m => (m._1 -> m._2(0)))

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
            case Some(keyTable) => (RefManyToOne(keyTable.pkColumn,keyTable.name)::keys,columns)
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
          columns = columns,
          pkColumns = dbTable.columns,
          embeddableTables = embeddableTables,
          manyToOne = manyToOne,
          oneToMany = Nil
    )
  }
  
  def run(dbTables: List[DBTable]) = {

    val keyTables: List[KeyTable] = filterTablesWithKey(dbTables)

    val mapKeyTablesByName: Map[String, KeyTable] = keyTables.groupBy(_.name).map(m => (m._1 -> m._2(0)))

    val mapKeyTablesByColumn: Map[String, KeyTable] = buildMapKey(keyTables)

    val mapEmbdTables: Map[String, _] = mapKeyTablesByColumn.values.map(_.embeddableTables).flatten.groupBy(_.name)

    val (embeddableTables, tables) = dbTables.partition((t) => mapEmbdTables.contains(t.name))

    embeddableTables.foreach((t) => println(t.name))

    tables.map(createTable(_,mapKeyTablesByColumn,mapKeyTablesByName))
  }


  /*



    def fillEmbeddableTables(kt: TableWithKey, tables: List[Table]): TableKeyWithEmbeddable =
      TableKeyWithEmbeddable(kt,GroupBuilder.getEmbeddableTables(kt.table.name,tables))

    def findPKColumn(columns: List[Column]):List[Column] = columns.filter(!_.pkPosition.isEmpty)

    def equal(t1: Table, t2: Table) = ((t1.name != t2.name) && (t1.owner != t2.owner))

    def getManyToOneTable(table: Table, pkColumn: Column,  mTables: List[Table]):Option[Table] =
      mTables.find((t) => findPKColumn(t.columns).exists((c) => c.name == pkColumn.name))

  //  def fillManyToOneTable(tables: List[Table]) =





    def getOneToManyTables(pkColumn: Column, tables: List[Table]) = tables.filter((t) => t.columns.exists((c) => checkColumn(c,pkColumn)))
  */
}
