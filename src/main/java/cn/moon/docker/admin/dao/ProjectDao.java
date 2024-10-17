package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.Project;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDao extends BaseDao<Project> {


    public Project findByName(String name) {
        return this.findOneByField("name", name);
    }

    public Page<Project> findByNameLike(String keyword, Pageable pageable) {
        JpaQuery<Project> q= new JpaQuery<>();
        q.like("name", keyword);
        return findAll(q, pageable);
    }
}
