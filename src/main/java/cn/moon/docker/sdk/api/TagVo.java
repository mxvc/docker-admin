package cn.moon.docker.sdk.api;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TagVo {
    String name;

    Date time;

    String  url;
}
