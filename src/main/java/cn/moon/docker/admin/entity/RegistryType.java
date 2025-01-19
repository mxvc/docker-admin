package cn.moon.docker.admin.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.ann.MsgTool;
import io.tmgg.web.base.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Msg("注册中心类型")
public enum RegistryType implements DictEnum {

    @Msg("阿里云")
    ALIYUN,

    @Msg("腾讯云")
    TENCENT;

    @Override
    public String getMessage() {
        return MsgTool.getMsg(this);
    }
}
