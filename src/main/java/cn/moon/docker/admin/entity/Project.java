package cn.moon.docker.admin.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.moon.docker.admin.service.RegistryService;
import cn.moon.validation.StartWithLetter;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.sys.org.entity.SysOrg;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Getter
@Setter
@Entity
@FieldNameConstants
public class Project extends BaseEntity {

    @StartWithLetter
    @NotNull
    @Column(unique = true)
    String name;

    @ManyToOne
    SysOrg sysOrg;


    //默认的dockerfile
    @NotNull
    String dockerfile;

    // 默认分支
    @NotNull
    String branch;

    @NotNull
    String gitUrl;


    @ManyToOne
    Registry registry;

    // 自动更新latest版本
    Boolean autoPushLatest;


    public Registry getRegistry() {
        if(registry == null){
          registry =  SpringUtil.getBean(RegistryService.class).checkAndFindDefault();
        }
        return registry;
    }

    @Override
    public void prePersist() {
        super.prePersist();
        if(autoPushLatest == null){
            autoPushLatest = false;
        }
    }
}
