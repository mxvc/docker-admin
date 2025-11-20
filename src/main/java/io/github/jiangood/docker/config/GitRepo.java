package io.github.jiangood.docker.config;

import lombok.Data;


@Data
public class GitRepo {


    /**
     * url, 支持前缀
     */
    private String url;

    private String username;

    private String password;


}
