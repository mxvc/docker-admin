package io.github.mxvc.docker.admin.entity;


import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("注册中心")
@Getter
@Setter
@Entity
@FieldNameConstants
@Table(name = "t_registry")
public class Registry extends BaseEntity {


    @Remark("仓库地址")
    @NotNull
    String url;


    @Remark("命名空间")
    String namespace;


    @Remark("账号")
    String username;


    @Remark("密码")
    String password;

    @Remark("是否默认")
    @NotNull
    Boolean defaultRegistry;


    String ak;
    String sk;

    // 腾讯 ap-guangzhou
    String region; //

    public String getFullUrl() {
        if (url != null) {
            return url + "/" + namespace;
        }

        return namespace;
    }


    @Override
    public String toString() {
        return getFullUrl();
    }
}
