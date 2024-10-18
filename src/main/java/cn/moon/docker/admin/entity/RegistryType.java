package cn.moon.docker.admin.entity;

import io.tmgg.web.base.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegistryType implements DictEnum {

    ALIYUN("阿里云"),
    TENCENT("腾讯云");

    String message;
}
