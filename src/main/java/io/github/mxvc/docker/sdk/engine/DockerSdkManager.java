package io.github.mxvc.docker.sdk.engine;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import io.github.mxvc.docker.admin.entity.Host;
import io.github.mxvc.docker.admin.entity.Registry;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.MyDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DockerSdkManager {


    public DockerClient getClient(Host host) {
        return this.getClient(host, new Registry());
    }


    public DockerClient getClient(Host host, Registry registry) {
        String dockerHost = getLocalDockerHost();
        String virtualHost = null;
        if (host != null && StrUtil.isNotEmpty(host.getDockerHost())) {
            dockerHost = host.getDockerHost();
            virtualHost = host.getDockerHostHeader();
        }


        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost);
        if (registry != null) {
            builder.withRegistryUrl(registry.getUrl())
                    .withRegistryUsername(registry.getUsername())
                    .withRegistryPassword(registry.getPassword());
        }


        DockerClientConfig config = builder.build();

        DockerHttpClient httpClient = new MyDockerHttpClient(config.getDockerHost(), config.getSSLConfig(), virtualHost);


        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);


        return dockerClient;
    }


    public String getLocalDockerHost() {
        boolean windows = SystemUtil.getOsInfo().isWindows();
        return windows ? "tcp://localhost:2375" : "unix:///var/run/docker.sock";
    }


    public Map<String, String> getAppLabelFilter(String name) {
        Map<String, String> labels = new HashMap<>();
        labels.put("app.name", name);
        return labels;
    }


}
