package schemereader.db.sql

object SQLBuilder {

  val sqlMaster =
    s"""|select distinct
        |       t.referenced_name as table_name,
        |       t.referenced_owner,
        |       (select cc.column_name
        |          from all_cons_columns cc, all_constraints c
        |         where cc.table_name = t.referenced_name
        |           and cc.owner = t.referenced_owner
        |           and c.constraint_name = cc.constraint_name
        |           and c.owner = cc.owner
        |           and c.constraint_type = 'P'
        |           and exists
        |                (select *
        |                   from all_tables k
        |                  where k.table_name = t.referenced_name || '_K'
        |                     and k.owner = t.referenced_owner)) as pk_column
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
        |       c.data_default
        |  from all_tab_columns c
        | where c.table_name = :table_name
        |   and c.owner = 'STGADM'""".stripMargin

}
