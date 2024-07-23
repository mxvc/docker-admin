package com.github.dockerjava.httpclient5;

import com.github.dockerjava.transport.SSLConfig;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

public class MyDockerHttpClient extends ApacheDockerHttpClientImpl {

    public MyDockerHttpClient(URI dockerHost, SSLConfig sslConfig, String vhost) {
        super(dockerHost, sslConfig, Integer.MAX_VALUE, null, null);
        this.vhost = vhost;
    }

    String vhost;

    @Override
    public Response execute(Request request) {
        if (StringUtils.isNotBlank(vhost)) {
            request.headers().put("Host", vhost.trim());
        }

        return super.execute(request);
    }
}
