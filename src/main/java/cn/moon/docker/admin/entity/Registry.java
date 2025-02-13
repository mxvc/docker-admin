package cn.moon.docker.admin.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("注册中心")
@Getter
@Setter
@Entity
@FieldNameConstants
@Table(name = "t_registry")
public class Registry extends BaseEntity {


    @Msg("仓库地址")
    @NotNull
    String url;


    @Msg("命名空间")
    String namespace;


    @Msg("账号")
    String username;


    @Msg("密码")
    String password;

    @Msg("是否默认")
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
