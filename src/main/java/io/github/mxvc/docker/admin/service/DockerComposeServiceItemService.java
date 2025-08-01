package io.github.mxvc.docker.admin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import io.github.mxvc.docker.admin.dao.DockerComposeDao;
import io.github.mxvc.docker.admin.dao.DockerComposeServiceItemDao;
import io.github.mxvc.docker.admin.entity.DockerCompose;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.github.mxvc.docker.admin.entity.Registry;
import io.github.mxvc.docker.sdk.engine.DefaultCallback;
import io.github.mxvc.docker.sdk.engine.DockerSdkManager;
import io.tmgg.web.persistence.BaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DockerComposeServiceItemService extends BaseService<DockerComposeServiceItem> {

    public static final String DOCKER_COMPOSE_ITEM_PID = "docker-compose-item-pid";
    @Resource
    DockerComposeServiceItemDao dao;

    @Resource
    DockerComposeDao dockerComposeDao;

    @Resource
    DockerSdkManager sdk;

    @Resource
    RegistryService registryService;

    public List<DockerComposeServiceItem> findByPid(String id) {
        return dao.findByPid(id);
    }


    public void delete(String id) {
        DockerComposeServiceItem item = dao.findOne(id);
        DockerCompose dockerCompose = dockerComposeDao.findOne(item.getPid());

        log.info("删除容器{}中...", item.getContainerName());

        DockerClient cli = getCli(dockerCompose);

        List<Container> all = cli.listContainersCmd().withShowAll(true).withLabelFilter(getDockerComposeFilter(item.getPid(), item.getId())).exec();
        Assert.state(all.isEmpty(), "容器还存在，请先停止并删除容器");

        dao.deleteById(id);
    }


    public void deploy(String id, String tag) throws InterruptedException {
        DockerComposeServiceItem cfg = dao.findOne(id);
        String pid = cfg.getPid();
        DockerCompose dockerCompose = dockerComposeDao.findOne(pid);


        DockerClient cli = getCli(dockerCompose);

        String image = cfg.getImage();
        if (!cfg.getImageTag().equals(tag)) {
            image = cfg.getImageUrl() + ":" + tag;
            cfg.setImage(image);
            dao.save(cfg);
        }


        log.info("开始拉取镜像 {}", image);
        cli.pullImageCmd(image).exec(new DefaultCallback<>(id)).awaitCompletion();


        log.info("停止旧容器中....");
        this.stopAndRemoveContainer(pid, id, cli);


        log.info("部署... {}", image);


        CreateContainerCmd containerCmd = cli.createContainerCmd(image);
        convertConfigToCmd(cfg, containerCmd);
        containerCmd.withName(cfg.getContainerName());
        containerCmd.withLabels(this.getDockerComposeFilter(pid, id));

        CreateContainerResponse response = containerCmd.exec();


        log.info("创建容器{}", response);

        String containerId = response.getId();

        log.info("启动容器");
        cli.startContainerCmd(containerId).exec();
    }

    private DockerClient getCli(DockerCompose dockerCompose) {
        Registry registry = registryService.checkAndFindDefault();
        DockerClient cli = sdk.getClient(dockerCompose.getHost(), registry);

        return cli;
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

                // 修复某些dockerfile 未配置 EXPOSE 8080
                List<ExposedPort> exposedPorts = portBindings.stream().map(PortBinding::getExposedPort).collect(Collectors.toList());
                cmd.withExposedPorts(exposedPorts);

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


    public void stopAndRemoveContainer(String pid, String id, DockerClient cli) {
        Map<String, String> filter = this.getDockerComposeFilter(pid, id);
        List<Container> list = cli.listContainersCmd()
                .withLabelFilter(filter)
                .withShowAll(true)
                .exec();
        for (Container container : list) {
            if (container.getState().equals("running")) {
                log.info("停止容器  {}", StrUtil.toString(container.getNames()));
                cli.stopContainerCmd(container.getId()).exec();
            }
            cli.removeContainerCmd(container.getId()).exec();
        }
    }

    public void deleteContainers(DockerCompose dockerCompose) {
        DockerClient cli = getCli(dockerCompose);

        Map<String, String> filter = new HashMap<>();
        filter.put(DOCKER_COMPOSE_ITEM_PID, dockerCompose.getId());

        List<Container> list = cli.listContainersCmd()
                .withLabelFilter(filter)
                .withShowAll(true)
                .exec();
        Assert.state(list.size() < 10, "可能有错");
        for (Container container : list) {
            Object names = container.getNames();
            log.info("准备删除容器: {}", names);
            if (container.getState().equals("running")) {

                log.info("停止容器  {}", names);
                cli.stopContainerCmd(container.getId()).exec();
            }
            cli.removeContainerCmd(container.getId()).exec();
        }

    }


    public Map<String, String> getDockerComposeFilter(String pid, String id) {
        Map<String, String> labels = new HashMap<>();
        labels.put(DOCKER_COMPOSE_ITEM_PID, pid);
        labels.put("docker-compose-item-id", id);
        return labels;
    }

    public void deleteByPid(String pid) {
        List<DockerComposeServiceItem> items = dao.findByPid(pid);
        dao.deleteAll(items);
        dao.flush();
    }

    public String getContainerName(DockerCompose dockerCompose, DockerComposeServiceItem item) {
        return dockerCompose.getName() + "." + item.getName();
    }

    public Map<String, Object> servicesStatus(String pid) {
        DockerCompose dockerCompose = dockerComposeDao.findOne(pid);

        DockerClient cli = getCli(dockerCompose);

        Map<String, String> labels = new HashMap<>();
        labels.put(DOCKER_COMPOSE_ITEM_PID, pid);

        List<Container> list = cli.listContainersCmd().withShowAll(true).withLabelFilter(labels).exec();

        Map<String, Object> rs = new HashMap<>();

        for (Container container : list) {
            rs.put(container.getNames()[0].replace("/", ""), container.getState());
        }

        return rs;
    }
}
