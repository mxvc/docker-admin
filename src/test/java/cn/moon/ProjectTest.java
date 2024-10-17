package cn.moon;


import cn.moon.docker.admin.service.ProjectService;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

@SpringBootTest
public class ProjectTest {

    @Resource
    ProjectService projectService;

    @Resource
    UserService userService;





}
