package cn.moon.docker.admin.entity;

import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;

@Getter
@Setter
@Entity
public class Registry extends BaseEntity {

    @NotNull
    String url;

    String username;
    String password;

    String namespace;


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