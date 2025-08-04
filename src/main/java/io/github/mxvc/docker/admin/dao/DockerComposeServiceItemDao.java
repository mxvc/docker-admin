package io.github.mxvc.docker.admin.dao;

import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DockerComposeServiceItemDao extends BaseDao<DockerComposeServiceItem> {
    public List<DockerComposeServiceItem> findByPid(String id) {
        JpaQuery<DockerComposeServiceItem> q = new JpaQuery<>();
        q.eq(DockerComposeServiceItem.Fields.pid , id);

        return this.findAll(q, Sort.by("seq","name"));
    }

    public DockerComposeServiceItem findByPidAndName(String id,String name) {
        JpaQuery<DockerComposeServiceItem> q = new JpaQuery<>();
        q.eq(DockerComposeServiceItem.Fields.pid , id);
        q.eq(DockerComposeServiceItem.Fields.name , name);
        return this.findOne(q);
    }
}
