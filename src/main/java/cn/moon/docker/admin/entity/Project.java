package cn.moon.docker.admin.entity;

import cn.moon.validation.StartWithLetter;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.modules.sys.entity.SysOrg;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Remark("项目")
@Getter
@Setter
@Entity
@FieldNameConstants
public class Project extends BaseEntity {

    @Remark("组织")
    @ManyToOne
    SysOrg sysOrg;


    @Remark("名称")
    @StartWithLetter
    @NotNull
    @Column(unique = true)
    String name;

    @NotNull
    String gitUrl;



    //默认的dockerfile
    @NotNull
    String dockerfile;

    // 默认分支
    @Remark("默认分支")
    @NotNull
    String branch;






    @Remark("注册中心")
    @ManyToOne
    Registry registry;

    // 自动更新latest版本
    @Remark("维护latest")
    Boolean autoPushLatest;


    String remark;



    @Override
    public void prePersist() {
        super.prePersist();
        if(autoPushLatest == null){
            autoPushLatest = false;
        }
    }
}
