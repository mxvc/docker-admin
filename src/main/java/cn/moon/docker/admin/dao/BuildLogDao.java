package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.BuildLog;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BuildLogDao extends BaseDao<BuildLog> {

    public List<BuildLog> findByProjectIdAndSuccessIsTrue(String projectId) {

        return findAllByField(BuildLog.Fields.projectId,projectId, BuildLog.Fields.success, true);
    }

    public List<BuildLog> findByProjectIdAndSuccessIsFalse(String projectId) {
        return findAllByField(BuildLog.Fields.projectId,projectId, BuildLog.Fields.success, false);
    }

    public List<BuildLog> findByProjectId(String projectId) {
        return findAllByField(BuildLog.Fields.projectId,projectId);
    }

    public List<BuildLog> findByProjectIdAndSuccessIsNull(String projectId) {
        return findAllByField(BuildLog.Fields.projectId,projectId, BuildLog.Fields.success,null);
    }

    public BuildLog findTop1ByProjectIdOrderByCreateTimeDesc(String projectId) {
        JpaQuery<BuildLog> q= new JpaQuery<>();
        q.eq(BuildLog.Fields.projectId,projectId);
        return findTop1(q, Sort.by(BaseEntity.FIELD_CREATE_TIME));
    }
}
