package schemereader.models

case class Column(name: String,
                  dataType: String,
                  defaultValue: Option[String],
                  dataLength: Int,
                  dataPrecision: Option[Int],
                  dataScale: Option[Int],
                  pkPosition: Option[Int])

case class RefManyToOne(column: Column, tableName: String)

case class RefOneToMany(columns: List[Column],tables: List[Table])

case class DBTable(name: String,
                   owner: String,
                   columns: List[Column],
                   pkColumns: List[Column])

case class EmbeddableTable(name: String,
                           owner: String,
                           columns: List[Column])

case class Table(name: String,
                 owner: String,
                 columns: List[Column],
                 pkColumns: List[Column],
                 embeddableTables: List[DBTable],
                 manyToOne: List[RefManyToOne],
                 oneToMany: List[RefOneToMany])

