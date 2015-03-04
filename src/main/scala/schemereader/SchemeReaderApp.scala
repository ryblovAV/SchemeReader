package schemereader

import grizzled.slf4j.Logging
import schemereader.db.reader.DBReader

object SchemeReaderApp extends App with Logging {

  val s = DBReader.getTables.filter(_.name == "CI_ACCT")

  val c = DBReader.getColumns("CI_ACCT")
  c.foreach(info(_))
//
//  println("executionStart = " + executionStart)

}
