package io.github.mxvc.docker.admin.entity;

import io.tmgg.data.domain.BaseEntity;
import io.tmgg.lang.ann.Remark;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;


@Remark("应用分组")
@Entity
@Getter
@Setter
@FieldNameConstants
public class AppGroup extends BaseEntity {

    @Remark("名称")
    private String name;


    @Remark("排序")
    private Integer seq;

}
