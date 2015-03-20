package schemereader.models

case class Column(name: String,
                  dataType: String,
                  defaultValue: Option[String],
                  dataLength: Int,
                  dataPrecision: Option[Int],
                  dataScale: Option[Int],
                  pkPosition: Option[Int])

case class RefManyToOne(columnName: String, tableName: String)

case class RefOneToMany(oneTableName: String, manyTableName: String, columnName: String)

case class DBTable(name: String,
                   owner: String,
                   columns: List[Column],
                   pkColumns: List[Column])

case class EmbeddableTable(name: String,
                           owner: String,
                           columns: List[Column])

case class Table(name: String,
                 owner: String,
                 embeddable: Int,
                 columns: List[Column],
                 pkColumns: List[Column],
                 embeddedTableNames: List[String],
                 manyToOne: List[RefManyToOne],
                 oneToMany: List[RefOneToMany])

case class KeyTable(name: String, owner: String, pkColumn: Column, embeddedTables: List[DBTable])

