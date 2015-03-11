import org.scalatest.{FunSpec, ShouldMatchers}
import schemereader.db.reader.{DBReader, GroupBuilder}
import schemereader.json.reader.{DBTableReaderWriter, DBTableParser}

class ReaderTest extends FunSpec with ShouldMatchers {


  describe("db read") {

    ignore("read tables,columns") {
      val tables = DBReader.getTablesWithColumns.filter(_.pkColumns.isEmpty)
      tables.foreach((t) => {
        info(s"-------- ${t.name} ------")
        info(s"columns ${t.pkColumns.map(_.name).mkString(";")}")
      })
//      info(DBTableParser.write(tables))
      true should be (true)
    }

    ignore("group") {
      val dbTables = DBReader.getTablesWithColumns.filter(_.pkColumns.isEmpty)
      val tables = GroupBuilder.run(dbTables)
      tables.foreach((t) => {
        info(s"-------${t.name}----------")
      })
    }

    it("write") {
      val tables = DBReader.getTablesWithColumns
      DBTableReaderWriter.writeSceme(tables)
    }


//
//      val keyTables = GroupBuilder.getTablesWithKey(tablesWithPKColumns)
//
//
//      val keyTablesWithEmbeddable = keyTables.map((kt) => GroupBuilder.fillEmbeddableTables(kt,tables))
//
//      keyTablesWithEmbeddable.foreach((t) => info(
//        s"""|${t.table.name} -> ${t.pkColumn.name}
//            |embeddables: ${t.embeddable.map(_.name).mkString(";")}
//         """.stripMargin))


//      info("------ many to one----------")
//      tables.foreach((t) => {
//        info(s"table = ${t.name}")
//        t.columns.foreach((c) => {
//          GroupBuilder.getManyToOneTable(t, c, mTables) match {
//            case Some(rTable) => info(s"table: ${t.name} column: ${c.name} ref: ${rTable.name}")
//            case _ =>
//          }
//
//        })
//      }
//      )




  }
}
