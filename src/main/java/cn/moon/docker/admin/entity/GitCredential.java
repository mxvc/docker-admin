package cn.moon.docker.admin.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;

@Msg("GIT凭据")
@Entity
@Getter
@Setter
@Table(name = "t_git_credential")
public class GitCredential extends BaseEntity {


    @Msg("前缀")
    @NotNull
    String url;

    @Msg("账号")
    @NotNull
    String username;

    @Msg("密码")
    @NotNull
    String password;


}
