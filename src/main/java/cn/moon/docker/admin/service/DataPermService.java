package cn.moon.docker.admin.service;

import cn.moon.docker.admin.dao.AppDao;
import cn.moon.docker.admin.dao.HostDao;
import cn.moon.docker.admin.dao.ProjectDao;
import cn.moon.docker.admin.dao.UserDao;
import cn.moon.docker.admin.entity.App;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.entity.Project;
import cn.moon.docker.admin.entity.User;
import cn.moon.lang.web.TreeOption;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DataPermService {

    @Resource
    ProjectDao projectDao;

    @Resource
    AppDao appDao;

    @Resource
    HostDao hostDao;

    @Resource
    UserDao userDao;


    public List<TreeOption> tree() {
        List<TreeOption> list = new ArrayList<>();
        list.add(new TreeOption("项目", "project-data", null));
        list.add(new TreeOption("应用", "app-data", null));
        list.add(new TreeOption("主机", "host-data", null));

        List<Project> projectList = projectDao.findAll();
        List<App> appList = appDao.findAll();
        List<Host> hostList = hostDao.findAll();


        for (Project project : projectList) {
            list.add(new TreeOption(project.getName(), "P-" + project.getId(), "project-data"));
        }
        for (App app : appList) {
            list.add(new TreeOption(app.getName(), "A-" + app.getId(), "app-data"));
        }
        for (Host host : hostList) {
            list.add(new TreeOption(host.getName(), "H-" + host.getId(), "host-data"));
        }

        List<TreeOption> tree = TreeOption.convertTree(list);

        return tree;
    }

    public void grant(String id, String[] keys) {
        User user = userDao.findById(id).orElse(null);

        user.setDataPerms(Arrays.asList(keys));

        userDao.save(user);

    }
}
