package io.github.jiangood.docker.config;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registry  {


    String url;


    String namespace;


    String username;


    String password;



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
