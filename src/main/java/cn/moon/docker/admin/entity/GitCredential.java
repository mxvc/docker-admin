package cn.moon.docker.admin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;

@Remark("GIT凭据")
@Entity
@Getter
@Setter
@Table(name = "t_git_credential")
public class GitCredential extends BaseEntity {


    @Remark("前缀")
    @NotNull
    String url;

    @Remark("账号")
    @NotNull
    String username;

    @Remark("密码")
    @NotNull
    String password;


}
