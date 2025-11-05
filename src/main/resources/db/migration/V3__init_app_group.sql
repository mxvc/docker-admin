insert  INTO t_app_group (id, name, seq) VALUES ('1', '默认分组',   -1);
update t_app set app_group_id = '1' where app_group_id is null;
