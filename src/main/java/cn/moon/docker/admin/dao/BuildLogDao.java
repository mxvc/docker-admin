package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.BuildLog;
import cn.moon.lang.web.persistence.BaseRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface BuildLogDao extends BaseRepository<BuildLog> {
    List<BuildLog> findByProjectIdAndSuccessIsTrue(String projectId);

    List<BuildLog> findByProjectIdAndSuccessIsFalse(String projectId);

    List<BuildLog> findByProjectId(String projectId);

    List<BuildLog> findByProjectIdAndSuccessIsNull(String projectId);

    BuildLog findTop1ByProjectIdOrderByCreateTimeDesc(String projectId);
}
