package schemereader.db.sql

object SQLBuilder {

  val sqlMaster =
    s"""|select distinct
        |       t.referenced_name as table_name,
        |       t.referenced_owner
        |          from dba_dependencies t
        |         where t.owner = 'LESKDATA'
        |           and t.type = 'PACKAGE BODY'
        |           and t.referenced_owner = 'STGADM'
        |           and t.referenced_type = 'TABLE'""".stripMargin

  val sqlColumns =
    s"""|select c.column_name,
        |       c.data_type,
        |       c.data_length,
        |       c.data_precision,
        |       c.data_scale,
        |       c.data_default,
        |       (select cc.position
        |         from all_constraints t,
        |              all_cons_columns cc
        |        where t.owner = c.owner
        |          and t.table_name = c.table_name
        |          and t.constraint_type = 'P'
        |          and cc.owner = t.owner
        |          and cc.table_name = t.table_name
        |          and cc.constraint_name = t.constraint_name
        |          and cc.column_name = c.column_name) as pk_position
        |  from all_tab_columns c
        | where c.table_name = :table_name
        |   and c.owner = 'STGADM'""".stripMargin

}
