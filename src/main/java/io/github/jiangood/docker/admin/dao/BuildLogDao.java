package io.github.jiangood.docker.admin.dao;

import io.admin.framework.data.query.JpaQuery;
import io.github.jiangood.docker.admin.entity.BuildLog;


import io.admin.framework.data.repository.BaseDao;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BuildLogDao extends BaseDao<BuildLog> {

    public List<BuildLog> findByProjectIdAndSuccessIsTrue(String projectId) {
        JpaQuery<BuildLog> q = new JpaQuery<>();
        q.eq(BuildLog.Fields.projectId,projectId);
        q.eq(BuildLog.Fields.success, true);
        return super.findAll(q);
    }

    public List<BuildLog> findByProjectIdAndSuccessIsFalse(String projectId) {
        JpaQuery<BuildLog> q = new JpaQuery<>();
        q.eq(BuildLog.Fields.projectId,projectId);
        q.eq(BuildLog.Fields.success, false);
        return super.findAll(q);
    }

    public List<BuildLog> findByProjectId(String projectId) {
        JpaQuery<BuildLog> q = new JpaQuery<>();
        q.eq(BuildLog.Fields.projectId,projectId);
        return super.findAll(q);
    }

    public List<BuildLog> findByProjectIdAndSuccessIsNull(String projectId) {
        JpaQuery<BuildLog> q = new JpaQuery<>();
        q.eq(BuildLog.Fields.projectId,projectId);
        q.isNull(BuildLog.Fields.success);
        return super.findAll(q);
    }

    public BuildLog findTop1ByProjectIdOrderByCreateTimeDesc(String projectId) {
        JpaQuery<BuildLog> q= new JpaQuery<>();
        q.eq(BuildLog.Fields.projectId,projectId);
        return findTop1(q, Sort.by("createTime"));
    }
}
