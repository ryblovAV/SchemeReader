package schemereader

import grizzled.slf4j.Logging
import schemereader.db.reader.GroupBuilder
import schemereader.json.reader.TableReaderWriter

object SchemeReaderApp extends App with Logging {
  val dbTables = TableReaderWriter.readScheme
  val tables = GroupBuilder.run(dbTables)
  TableReaderWriter.writeTables(tables)
}
