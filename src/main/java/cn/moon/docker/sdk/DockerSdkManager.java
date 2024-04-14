package cn.moon.docker.sdk;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.admin.service.RegistryService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DockerSdkManager {


    @Resource
    RegistryService registryConfig;


    public DockerClient getClient(Host host) {
        return this.getClient(host, new Registry());
    }

    public DockerClient getClient(Host host, String imageUrl) {
        Registry registry = registryConfig.findByUrl(imageUrl);
        return this.getClient(host, registry);
    }


    public DockerClient getClient(Host host, Registry registry) {
        String dockerHost = getLocalDockerHost();
        String dockerHostHeader = null;
        if (host != null && StrUtil.isNotEmpty(host.getDockerHost())) {
            dockerHost = host.getDockerHost();
            dockerHostHeader = host.getDockerHostHeader();
        }

        log.info("docker host: {}", dockerHost);


        log.info("注册中心 {} ", registry);
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost);
        if (registry != null) {
            builder.withRegistryUrl(registry.getUrl())
                    .withRegistryUsername(registry.getUsername())
                    .withRegistryPassword(registry.getPassword());
        }

        DockerClientConfig config = builder.build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())

                .virtualHost(dockerHostHeader) // 使用dockerId 作为路由转发的标识
                .build();
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        return dockerClient;
    }


    public String getLocalDockerHost() {
        return SystemUtil.getOsInfo().isWindows() ?
                "tcp://localhost:2375" :
                "unix:///var/run/docker.sock";
    }


    public Map<String, String> getAppLabelFilter(String name) {
        Map<String, String> labels = new HashMap<>();
        labels.put("app.name", name);
        return labels;
    }

}
