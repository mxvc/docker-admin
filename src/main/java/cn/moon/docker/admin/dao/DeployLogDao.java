package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.DeployLog;
import cn.moon.lang.web.persistence.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeployLogDao extends BaseRepository<DeployLog> {
}
