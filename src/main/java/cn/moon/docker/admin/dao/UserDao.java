package cn.moon.docker.admin.dao;


import cn.moon.docker.admin.entity.User;
import cn.moon.lang.web.persistence.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends BaseRepository<User> {

    User findByUsername(String username);
}
