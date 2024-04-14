package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.Registry;
import cn.moon.lang.web.persistence.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistryDao extends BaseRepository<Registry> {

    Registry findByDefaultRegistryIsTrue();

}
