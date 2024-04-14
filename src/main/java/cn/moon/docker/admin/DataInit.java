package cn.moon.docker.admin;

import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.entity.User;
import cn.moon.docker.admin.service.HostService;
import cn.moon.docker.admin.service.UserService;
import cn.moon.base.role.Role;
import cn.moon.docker.sdk.DockerSdkManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 启动后执行
 */
@Component
@Slf4j
public class DataInit implements ApplicationRunner {


    @Resource
    UserService userService;

    @Resource
    HostService hostService;

    @Resource
    DockerSdkManager sdkManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userService.count() == 0) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("123456");
            user.setRole(Role.admin);
            user.setName("超级管理员");
            userService.save(user);
            log.info("创建默认账号 {}", user);
        }

        if (hostService.count() == 0) {
            Host host = new Host();
            host.setName("默认主机");
            host.setDockerHost(sdkManager.getLocalDockerHost());
            host.setIsRunner(true);
            hostService.save(host);
            log.info("创建默认主机配置 {}", host);
        }


    }
}
