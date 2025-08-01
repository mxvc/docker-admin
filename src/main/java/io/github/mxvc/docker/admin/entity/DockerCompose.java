package io.github.mxvc.docker.admin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.validator.ValidateStartWithLetter;
import io.tmgg.modules.system.entity.SysOrg;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Remark("容器组")
@Entity
@Getter
@Setter
@FieldNameConstants
public class DockerCompose extends BaseEntity {

    @NotNull
    @Remark("显示名")
    String label;

    @NotNull
    @ValidateStartWithLetter
    @Remark("名称")
    @Column(unique = true, length = 50)
    String name;

    @ManyToOne
    SysOrg sysOrg;


    @NotNull
    @ManyToOne
    Host host;


}
