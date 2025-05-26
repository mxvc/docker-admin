package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.Registry;


import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class RegistryDao extends BaseDao<Registry> {

    public Registry findByDefaultRegistryIsTrue() {
        JpaQuery<Registry> q = new JpaQuery<>();
        q.eq(Registry.Fields.defaultRegistry, true);


        return this.findOne(q);
    }

}
