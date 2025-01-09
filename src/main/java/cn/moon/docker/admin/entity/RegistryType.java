package cn.moon.docker.admin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.ann.RemarkTool;
import io.tmgg.web.base.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Remark("注册中心类型")
public enum RegistryType implements DictEnum {

    @Remark("阿里云")
    ALIYUN,

    @Remark("腾讯云")
    TENCENT;

    @Override
    public String getMessage() {
        return RemarkTool.getRemark(this);
    }
}
