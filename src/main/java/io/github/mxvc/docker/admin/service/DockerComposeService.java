package io.github.mxvc.docker.admin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import io.github.mxvc.docker.admin.dao.DockerComposeDao;
import io.github.mxvc.docker.admin.entity.DockerCompose;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.github.mxvc.docker.admin.entity.Registry;
import io.github.mxvc.docker.sdk.engine.DefaultCallback;
import io.github.mxvc.docker.sdk.engine.DockerSdkManager;
import io.tmgg.web.persistence.BaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DockerComposeService extends BaseService<DockerCompose> {

    @Resource
    DockerComposeDao dao;

    @Resource
    DockerSdkManager sdk;

    @Resource
    RegistryService registryService;


    public void deploy(String id, String name) throws IOException, InterruptedException {
        DockerCompose dockerCompose = dao.findOne(id);

        List<DockerComposeServiceItem> items = DockerComposeServiceItem.load("");
        DockerComposeServiceItem cfg = items.stream().filter(t -> t.getName().equals(name)).findFirst().orElse(null);

        Registry registry = registryService.checkAndFindDefault();
        DockerClient cli = sdk.getClient(dockerCompose.getHost(), registry);

        String image = cfg.getImage();

        log.info("开始拉取镜像 {}", image);
        cli.pullImageCmd(image).exec(new DefaultCallback<>(id)).awaitCompletion();


        log.info("停止旧容器中....");
        this.stopAndRemove(id, name, cli);


        log.info("部署... {}", image);


        CreateContainerCmd containerCmd = cli.createContainerCmd(image);
        convertConfigToCmd(cfg, containerCmd);
        containerCmd.withName(getContainerId(id, name));
        containerCmd.withLabels(sdk.getDockerComposeFilter(id, name));

        CreateContainerResponse response = containerCmd.exec();


        log.info("创建容器{}", response);

        String containerId = response.getId();

        log.info("启动容器");
        cli.startContainerCmd(containerId).exec();


    }

    public void delete(String id, String name) {
        DockerCompose dockerCompose = dao.findOne(id);


        String containerId = getContainerId(id, name);
        Registry registry = registryService.checkAndFindDefault();
        DockerClient cli = sdk.getClient(dockerCompose.getHost(), registry);

        try {


            InspectContainerResponse res = cli.inspectContainerCmd(containerId).exec();

            if (res.getState().getStatus().equals("running")) {
                cli.stopContainerCmd(containerId).exec();
            }
            cli.removeContainerCmd(containerId).exec();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void convertConfigToCmd(DockerComposeServiceItem cfg, CreateContainerCmd cmd) {
        {
            HostConfig hostConfig = HostConfig.newHostConfig();

            // 网络、 端口
            if (StrUtil.isNotBlank(cfg.getNetworkMode())) {
                hostConfig.withNetworkMode(cfg.getNetworkMode());
            }

            // 仅桥接模式下使用端口映射
            List<String> ports = cfg.getPorts();
            if (CollUtil.isNotEmpty(ports) && (StrUtil.isBlank(cfg.getNetworkMode()) || cfg.getNetworkMode().equals("bridge"))) {
                List<PortBinding> portBindings = ports.stream().map(PortBinding::parse).toList();
                hostConfig.withPortBindings(portBindings);
            }


            // 文件路径绑定
            List<String> volumes = cfg.getVolumes();
            if (CollUtil.isNotEmpty(volumes)) {
                List<Bind> binds = volumes.stream().map(Bind::parse).toList();
                hostConfig.withBinds(binds);
            }


            // 重启策略
            if (StrUtil.isNotEmpty(cfg.getRestart())) {
                hostConfig.withRestartPolicy(RestartPolicy.parse(cfg.getRestart()));
            }


            hostConfig.withPrivileged(cfg.getPrivileged());

            // hosts，ip域名映射, 支持两种格式， 1. ip 域名 2.域名:ip
            if (CollUtil.isNotEmpty(cfg.getExtraHosts())) {
                hostConfig.withExtraHosts(cfg.getExtraHosts().toArray(String[]::new));
            }


            // 日志限制
            LogConfig logConfig = new LogConfig(LogConfig.LoggingType.DEFAULT, new HashMap<>());
            logConfig.getConfig().put("max-size", "200m");
            hostConfig.withLogConfig(logConfig);


            cmd.withHostConfig(hostConfig);
        }

        // 环境变量
        if (CollUtil.isNotEmpty(cfg.getEnvironment())) {
            List<String> envs = new ArrayList<>();
            for (Map.Entry<String, String> e : cfg.getEnvironment().entrySet()) {
                envs.add(e.getKey() + "=" + e.getValue());
            }
            cmd.withEnv(envs);
        }

        if (StrUtil.isNotEmpty(cfg.getCommand())) {
            List<String> list = StrUtil.splitTrim(cfg.getCommand(), " ");
            cmd.withCmd(list);
        }
    }


    public void stopAndRemove(String id, String name, DockerClient cli) {
        Map<String, String> filter = sdk.getDockerComposeFilter(id, name);
        List<Container> list = cli.listContainersCmd()
                .withLabelFilter(filter)
                .withShowAll(true)
                .exec();
        for (Container container : list) {
            if (container.getState().equals("running")) {
                log.info("停止容器  ｛｝", container.getNames());
                cli.stopContainerCmd(container.getId()).exec();
            }
            cli.removeContainerCmd(container.getId()).exec();
        }
    }


    private static String getContainerId(String id, String name) {
        return id + "_" + name;
    }


}

