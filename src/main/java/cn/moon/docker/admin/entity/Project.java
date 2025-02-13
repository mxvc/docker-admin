package cn.moon.docker.admin.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.validator.ValidateStartWithLetter;
import io.tmgg.modules.sys.entity.SysOrg;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Msg("项目")
@Getter
@Setter
@Entity
@FieldNameConstants
@Table(name = "t_project")
public class Project extends BaseEntity {

    @Msg("组织")
    @ManyToOne
    SysOrg sysOrg;


    @Msg("名称")
    @ValidateStartWithLetter
    @NotNull
    @Column(unique = true)
    String name;

    @NotNull
    String gitUrl;



    //默认的dockerfile
    @NotNull
    String dockerfile;

    // 默认分支
    @Msg("默认分支")
    @NotNull
    String branch;






    @Msg("注册中心")
    @ManyToOne
    Registry registry;

    // 自动更新latest版本
    @Msg("推送latest")
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
