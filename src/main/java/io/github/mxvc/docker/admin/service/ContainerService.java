package io.github.mxvc.docker.admin.service;

import io.github.mxvc.docker.admin.entity.Host;
import io.github.mxvc.docker.sdk.engine.DockerSdkManager;
import com.github.dockerjava.api.DockerClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Component
public class ContainerService {

    @Resource
    HostService hostService;

    @Resource
    DockerSdkManager dockerService;

    @Transactional
    public DockerClient responseLog(String hostId) {
        Host host = hostService.findOne(hostId);
        DockerClient client = dockerService.getClient(host);

return client;


    }
}
