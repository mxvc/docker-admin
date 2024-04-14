package cn.moon.docker.admin.entity;

import cn.moon.lang.web.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

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