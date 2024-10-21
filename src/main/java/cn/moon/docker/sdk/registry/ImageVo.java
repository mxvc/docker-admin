package cn.moon.docker.sdk.registry;

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

    Date time;
    String latestVersion;

    String description;
    int starCount;

    boolean isOfficial;


    Long tagCount;

    Long pullCount;
}
