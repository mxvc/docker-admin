package io.github.jiangood;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.io.IOException;
import java.util.List;

public class DockerSdkTest {

    public static void main(String[] args) throws IOException {

        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375");


        DockerClientConfig config = builder.build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        DockerClient cli = DockerClientImpl.getInstance(config, httpClient);

        System.out.println("------------------------------------------");

        run(cli);


        System.out.println("------------------------------------------");
        cli.close();


    }

    private static void run(DockerClient cli) {

        List<SearchItem> list = cli.searchImagesCmd("nginx").exec();

        for (SearchItem searchItem : list) {
            System.out.println("搜索结果："+searchItem);
        }


    }
}
