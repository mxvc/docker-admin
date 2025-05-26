package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.Project;


import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDao extends BaseDao<Project> {


    public Page<Project> findByNameLike(String keyword, Pageable pageable) {
        JpaQuery<Project> q= new JpaQuery<>();
        q.like("name", keyword);
        return findAll(q, pageable);
    }
}
