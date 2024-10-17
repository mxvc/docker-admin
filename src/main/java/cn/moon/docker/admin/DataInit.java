package cn.moon.docker.admin;

import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.service.HostService;
import cn.moon.base.Role;
import cn.moon.docker.sdk.DockerSdkManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 启动后执行
 */
@Component
@Slf4j
public class DataInit implements ApplicationRunner {


    @Resource
    HostService hostService;

    @Resource
    DockerSdkManager sdkManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
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
