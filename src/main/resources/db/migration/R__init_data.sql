REPLACE  INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('appGroup', 'settings', NULL, '应用分组', '/admin/appGroup', 1, 'MENU');
REPLACE  INTO t_app_group (id, name, seq) VALUES ('1', '默认分组',   -1);
update t_app set app_group_id = '1' where app_group_id is null;
