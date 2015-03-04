package schemereader.db.jdbc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import JdbcTemplatesUtl._

import schemereader.db.sql.SQLBuilder
import schemereader.models.Table

@Repository("jdbcUtl")
class JDBCTemplatesUtlImpl extends JdbcTemplatesUtl {

  @Autowired
  protected var jdbcTemplate:NamedParameterJdbcTemplate = _


}
