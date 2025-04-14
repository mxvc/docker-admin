package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.Registry;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class RegistryDao extends BaseDao<Registry> {

    public Registry findByDefaultRegistryIsTrue() {
        JpaQuery<Registry> q = new JpaQuery<>();
        q.eq(Registry.Fields.defaultRegistry, true);


        return this.findOne(q);
    }

}
