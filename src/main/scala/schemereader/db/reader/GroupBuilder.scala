package schemereader.db.reader

import schemereader.models.{Column, Table}

import scala.util.matching.Regex

object GroupBuilder {


  def getTablesWithKey(tables: List[Table]):List[Table] =
    tables.filter((t)=>tables.exists((t2) => t2.name == t.name + "_K"))

  def isEmbeddable(p: Regex, tableName: String):Boolean =
    !p.findFirstIn(tableName).isEmpty

  def getEmbeddableTables(mTabName: String, tables: List[Table]):List[Table] = {
    val p = (s"^(${mTabName}_CHAR|${mTabName}_K|${mTabName}_ID|${mTabName}_NAME|${mTabName}_PHONE)"+"$").r
    tables.filter((t) => isEmbeddable(p,t.name))
  }

  def findPKColumn(t: Table):List[Column] = t.columns.filter(!_.pkPosition.isEmpty)

  def getManyToOneTable(pkColumn: Column, mTables: List[Table]):Option[Table] =
    mTables.find((t) => findPKColumn(t).exists((c) => c.name == pkColumn.name))

  def checkColumn(c: Column, pkColumn: Column):Boolean =
    (c.dataType == pkColumn.dataType) && (c.dataLength == pkColumn.dataLength) && ((c.name.startsWith(pkColumn.name)) || (c.name.endsWith(pkColumn.name)))

  def getOneToManyTables(pkColumn: Column, tables: List[Table]) = tables.filter((t) => t.columns.exists((c) => checkColumn(c,pkColumn)))
}
