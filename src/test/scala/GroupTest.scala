import org.scalatest._
import schemereader.db.reader.GroupBuilder
import schemereader.models.Column

class GroupTest extends FunSpec with ShouldMatchers {
  describe("relation") {

    it("check column") {
      GroupBuilder.checkColumn(Column("CHAR_PREM_ID","VARCHAR2",None,10,None,None,None),
        Column("PREM_ID","VARCHAR2",None,10,None,None,Some(1))) should be (true)
      GroupBuilder.checkColumn(Column("CHAR_PREM_ID1","VARCHAR2",None,10,None,None,None),
        Column("PREM_ID","VARCHAR2",None,10,None,None,Some(1))) should be (false)
      GroupBuilder.checkColumn(Column("CHAR_PREM_ID","NUMBER",None,10,None,None,None),
        Column("PREM_ID","VARCHAR2",None,10,None,None,Some(1))) should be (false)
      GroupBuilder.checkColumn(Column("CHAR_PREM_ID","NUMBER",None,1,None,None,None),
        Column("PREM_ID","VARCHAR2",None,10,None,None,Some(1))) should be (false)


    }
  }
}
