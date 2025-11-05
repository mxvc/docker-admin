package io.github.mxvc.docker.admin.dao;

import io.github.mxvc.docker.admin.entity.Registry;


import io.tmgg.data.repository.BaseDao;
import io.tmgg.data.query.JpaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class RegistryDao extends BaseDao<Registry> {

    public Registry findByDefaultRegistryIsTrue() {
        JpaQuery<Registry> q = new JpaQuery<>();
        q.eq(Registry.Fields.defaultRegistry, true);


        return this.findOne(q);
    }

}
