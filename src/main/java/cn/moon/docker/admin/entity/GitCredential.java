package cn.moon.docker.admin.entity;

import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;

@Entity
@Getter
@Setter
public class GitCredential extends BaseEntity {


    @NotNull
    String url;


    @NotNull
    String username;

    @NotNull
    String password;


}
