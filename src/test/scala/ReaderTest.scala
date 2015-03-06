import org.scalatest.{FunSpec, ShouldMatchers}
import schemereader.db.reader.{GroupBuilder, DBReader}

class ReaderTest extends FunSpec with ShouldMatchers {


  describe("db read") {
    ignore("read tables,columns"){

      val tables = DBReader.getTables.map((t) => t.copy(columns = DBReader.getColumns(t.name)))

      val mTables = GroupBuilder.getTablesWithKey(tables)

      mTables.foreach(
        (t) => {
          info(s"------------${t.name}---------------")
          //t.columns.foreach((c) => info(s"$c"))
        }
      )

    }
  }


}
