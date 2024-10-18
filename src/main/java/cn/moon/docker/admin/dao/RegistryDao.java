package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.Registry;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class RegistryDao extends BaseDao<Registry> {

    public Registry findByDefaultRegistryIsTrue() {
        return this.findOneByField(Registry.Fields.defaultRegistry, true);
    }

}
