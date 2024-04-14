package cn.moon.docker.admin.dao;

import cn.moon.docker.admin.entity.Project;
import cn.moon.lang.web.persistence.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDao extends BaseRepository<Project> {


    Project findByName(String name);

    Page<Project> findByNameLike(String keyword, Pageable pageable);
}
