package io.github.jiangood.docker.sdk.registry;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ImageVo {
    String name;
    String type;
    String summary;

    String url;

    Date updateTime;
    Date createTime;
    String latestVersion;

    String description;
    int starCount;

    boolean isOfficial;


    Long tagCount;

    Long pullCount;
}
