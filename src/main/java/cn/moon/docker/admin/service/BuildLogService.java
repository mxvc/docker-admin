package cn.moon.docker.admin.service;

import cn.moon.docker.admin.dao.BuildLogDao;
import cn.moon.docker.admin.entity.BuildLog;
import cn.moon.lang.web.persistence.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuildLogService extends BaseService<BuildLog> {

    @Resource
    BuildLogDao dao;

    public List<String> versions(String projectId) {
        List<BuildLog> list = dao.findByProjectIdAndSuccessIsTrue(projectId);

        List<String> versions = list.stream().map(BuildLog::getVersion).distinct().collect(Collectors.toList());

        Collections.reverse(versions);
        return versions;
    }
}
