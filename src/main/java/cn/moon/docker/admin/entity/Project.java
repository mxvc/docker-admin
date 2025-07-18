package cn.moon.docker.admin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.validator.ValidateStartWithLetter;
import io.tmgg.modules.system.entity.SysOrg;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Table;
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
@Table(name = "t_project")
public class Project extends BaseEntity {

    @Remark("组织")
    @ManyToOne
    SysOrg sysOrg;


    @Remark("名称")
    @ValidateStartWithLetter
    @NotNull
    @Column(unique = true)
    String name;

    @NotNull
    String gitUrl;



    //默认的dockerfile
    @NotNull
    String dockerfile;

    @Remark("构建参数")
    String buildArg;

    // 默认分支
    @Remark("分支")
    @NotNull
    String branch;






    @Remark("注册中心")
    @ManyToOne
    Registry registry;

    // 自动更新latest版本
    @Remark("推送latest")
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
