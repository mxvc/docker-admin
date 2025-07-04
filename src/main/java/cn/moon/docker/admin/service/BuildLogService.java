package cn.moon.docker.admin.service;

import cn.moon.docker.admin.dao.BuildLogDao;
import cn.moon.docker.admin.entity.BuildLog;

import io.tmgg.web.persistence.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
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
        Collections.sort(versions);
        Collections.reverse(versions);
        return versions;
    }

    @Transactional
    public BuildLog saveLog(BuildLog buildLog) {
        return dao.saveAndFlush(buildLog);
    }

    public List<BuildLog> findByProject(String projectId) {
        return  dao.findByProjectId(projectId);
    }

    @Transactional
    public void cleanErrorLog(String projectId) {
        List<BuildLog> list = dao.findByProjectIdAndSuccessIsFalse(projectId);
        dao.deleteAll(list);
    }

    public List<BuildLog> findByProjectProcessing(String projectId) {
        return dao.findByProjectIdAndSuccessIsNull(projectId);
    }

    public BuildLog findTop1ByProject(String projectId) {
        BuildLog buildLog = dao.findTop1ByProjectIdOrderByCreateTimeDesc(projectId);
        return buildLog;
    }
}
