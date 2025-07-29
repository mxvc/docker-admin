package io.github.mxvc.docker.admin.dao;

import io.github.mxvc.docker.admin.entity.Project;


import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
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
