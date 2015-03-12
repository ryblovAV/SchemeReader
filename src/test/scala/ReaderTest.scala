import org.scalatest.{FunSpec, ShouldMatchers}
import schemereader.db.reader.{DBReader, GroupBuilder}
import schemereader.json.reader.{TableReaderWriter, TableParser}

class ReaderTest extends FunSpec with ShouldMatchers {

  describe("db read") {

    ignore("read tables from DB (jdbc)") {
      val tables = DBReader.getTablesWithColumns
      tables.isEmpty should be (false)
    }

  }
}
