REPLACE  INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('appGroup', 'settings', NULL, '应用分组', '/admin/appGroup', 1, 'MENU');
REPLACE  INTO app_group (id, name, seq) VALUES ('1', '默认分组',   -1);
