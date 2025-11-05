package io.github.mxvc.docker.admin.dao;


import io.github.mxvc.docker.admin.entity.Host;


import io.tmgg.data.repository.BaseDao;
import io.tmgg.data.query.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class HostDao extends BaseDao<Host> {

    public Host findTop1ByIsRunnerOrderByModifyTimeDesc(boolean isRunner) {
        JpaQuery<Host> q = new JpaQuery<>();
        q.eq(Host.Fields.isRunner, isRunner);
        return this.findTop1(q, Sort.by(Sort.Direction.DESC, "updateTime"));
    }


}
