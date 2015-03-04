package schemereader.db.jdbc

import java.sql.ResultSet

object JDBCExtractorSafe {

  def extractOptValue[T](p:(ResultSet,String),f:(ResultSet,String) => T):Option[T] = p match {
    case (rs,name) => {
      val v = f(rs,name)
      if (rs.wasNull) None else Some(v)
    }
  }

  def extractValue[T](p:(ResultSet,String),f:(ResultSet,String) => T):T = p match {
    case (rs,name) => f(rs,name)
  }

  implicit def rsToOptInt(p:(ResultSet,String)):Option[Int] = extractOptValue(p,(rs,name) => rs.getInt(name))

  implicit def rsToOptString(p:(ResultSet,String)):Option[String] = extractOptValue(p,(rs,name) => rs.getString(name))

  implicit def rsToInt(p:(ResultSet,String)):Int = extractValue(p,(rs,name) => rs.getInt(name))

  implicit def rsToString(p:(ResultSet,String)):String = extractValue(p,(rs,name) => rs.getString(name))

}
