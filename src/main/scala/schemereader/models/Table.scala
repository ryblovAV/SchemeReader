package schemereader.models

case class Column(name: String,
                  dataType: String,
                  defaultValue: Option[String],
                  dataLength: Int,
                  dataPrecision: Option[Int],
                  dataScale: Option[Int],
                  isPrimary: Boolean)

case class Table(name: String, owner: String, columns: List[Column])
