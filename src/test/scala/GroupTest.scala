import org.scalatest._
import schemereader.db.reader.GroupBuilder
import schemereader.json.reader.TableReaderWriter
import schemereader.models.{RefManyToOne, DBTable, Table, Column}

class GroupTest extends FunSpec with ShouldMatchers {
  describe("relation") {

    val dbTables = TableReaderWriter.readScheme
    val tables = GroupBuilder.run(dbTables)
    val saTable = tables.find((p) => (p.name == "CI_SA")).get
    val acctTable = tables.find((p) => (p.name == "CI_ACCT")).get
    val keyTables = GroupBuilder.filterTablesWithKey(dbTables)

    it("check read dbtable from json") {
      dbTables.isEmpty should be (false)
    }

    it("check dbTable CI_PER") {
      val ciPer = dbTables.find((t) => (t.name == "CI_PER")).get
      ciPer match {
        case DBTable(name,owner,columns, pkColumns) => {
          owner should be ("STGADM")
          columns.size should be (28)
          pkColumns.size should be(1)
        }
      }
    }

    it("check dbTable CI_PER_CHAR") {
      val ciPerChar = dbTables.find((t) => (t.name == "CI_PER_CHAR")).get
      ciPerChar.pkColumns.isEmpty should be (false)
    }

    it("build table") {
      tables.isEmpty should be (false)
    }

    it("check table exists") {
      val t = tables.find((p) => (p.name == "CI_PER"))
      t.isEmpty should be (false)
      t.get match {
        case Table(name,owner,embedded,columns,pkColumns,embeddedTables,manyToOne,oneToMany) => {
          owner should be ("STGADM")
          columns.isEmpty should be (false)
          columns.size should be (28)
          pkColumns.size should be (1)
        }
      }
    }

    it("check manyToOne") {

      saTable match {
        case Table(name,owner,embedded,columns,pkColumns,embeddedTables,manyToOne,oneToMany) => {
          manyToOne.isEmpty should be (false)
          val acctManyToOne = manyToOne.find((r) => r.tableName == "CI_ACCT").get
          acctManyToOne match {
            case RefManyToOne(columnName,tableName) => {
              columnName should be ("ACCT_ID")
              tableName should be ("CI_ACCT")
            }
          }
        }
      }
    }

    it("check OneToMany") {
      acctTable.oneToMany.isEmpty should be (false)
      acctTable.oneToMany.exists((r) => (r.oneTableName == "CI_ACCT") && (r.manyTableName == "CI_SA")) should be (true)
    }

    it("build map embedded table") {

      val m:Map[String,String] = GroupBuilder.buildMapPKByEmbTable(keyTables)

      m.get("CI_PER_CHAR").get should be ("PER_ID")
      m.get("CI_PER_K").get should be ("PER_ID")
      m.get("CI_PER_ID").get should be ("PER_ID")
      m.get("CI_PER_NAME").get should be ("PER_ID")

      m.get("CI_SA_CHAR").get should be ("SA_ID")
      m.get("CI_SA_K").get should be ("SA_ID")

    }

  }
}
