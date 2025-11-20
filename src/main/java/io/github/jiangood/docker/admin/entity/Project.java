package io.github.jiangood.docker.admin.entity;

import io.admin.common.utils.ann.Remark;
import io.admin.framework.validator.ValidateStartWithLetter;
import io.admin.modules.system.entity.SysOrg;
import io.admin.framework.data.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

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


    String remark;


}
