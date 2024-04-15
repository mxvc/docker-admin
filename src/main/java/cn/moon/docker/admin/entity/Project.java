package cn.moon.docker.admin.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.moon.docker.admin.service.RegistryService;
import cn.moon.lang.web.persistence.BaseEntity;
import cn.moon.validation.StartWithLetter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@FieldNameConstants
public class Project extends BaseEntity {

    @StartWithLetter
    @NotNull
    @Column(unique = true)
    String name;


    //默认的dockerfile
    String dockerfile;
    String defaultVersion;

    // 默认分支
    String branch;

    @NotNull
    String gitUrl;


    @ManyToOne
    Registry registry;


    public Registry getRegistry() {
        if(registry == null){
          registry =  SpringUtil.getBean(RegistryService.class).checkAndFindDefault();
        }
        return registry;
    }



}
