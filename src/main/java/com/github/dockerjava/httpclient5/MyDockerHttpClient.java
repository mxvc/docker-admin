package com.github.dockerjava.httpclient5;

import cn.hutool.core.bean.BeanUtil;
import com.github.dockerjava.transport.SSLConfig;
import org.apache.hc.core5.http.HttpHost;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public class MyDockerHttpClient extends ApacheDockerHttpClientImpl {

    public MyDockerHttpClient(URI dockerHost, SSLConfig sslConfig, String vhost) {
        super(dockerHost, sslConfig, Integer.MAX_VALUE, null, null);

        if (dockerHost.getScheme().equals("tcp")) {
            try {
                InetAddress addr = InetAddress.getByName(dockerHost.getHost());
                HttpHost httpHost = getHttpHost(dockerHost, vhost, addr);
                this.setHostField(httpHost);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private HttpHost getHttpHost(URI dockerHost, String vhost, InetAddress addr) {
        if (vhost != null) {
            return new HttpHost("http", addr, vhost, dockerHost.getPort());
        }
        return new HttpHost("http", addr, dockerHost.getPort());
    }


    public void setHostField(HttpHost httpHost) {
        BeanUtil.setFieldValue(this, "host", httpHost);
    }

}
