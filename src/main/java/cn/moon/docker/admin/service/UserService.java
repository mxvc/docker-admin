package cn.moon.docker.admin.service;

import cn.moon.docker.admin.dao.UserDao;
import cn.moon.docker.admin.entity.User;
import cn.moon.lang.web.persistence.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserService extends BaseService<User> {


    @Resource
    UserDao userDao;




    public long count() {

        return userDao.count();
    }
}
