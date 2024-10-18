package cn.moon.docker.admin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import lombok.experimental.FieldNameConstants;

@Remark("注册中心")
@Getter
@Setter
@Entity
@FieldNameConstants
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
    Boolean defaultRegistry;


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
