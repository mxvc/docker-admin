package io.github.jiangood.docker.admin.dao;

import io.github.jiangood.docker.admin.entity.Project;


import io.admin.framework.data.repository.BaseDao;
import io.admin.framework.data.query.JpaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDao extends BaseDao<Project> {


    public Page<Project> findByNameLike(String searchText, Pageable pageable) {
        JpaQuery<Project> q= new JpaQuery<>();
        q.like("name", searchText);
        return findAll(q, pageable);
    }
}
