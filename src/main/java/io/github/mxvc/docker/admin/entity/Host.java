package io.github.mxvc.docker.admin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * 主机信息
 */
@Remark("主机")
@Entity
@Getter
@Setter
@ToString
@FieldNameConstants
@Table(name = "t_host")
public class Host extends BaseEntity {

    @Remark("名称")
    @NotNull
    @Column(unique = true)
    String name;

    @Remark("构建节点")
    Boolean isRunner;


    String dockerHost;

    @Remark("指定host头")
    String dockerHostHeader;

    @Remark("备注")
    String remark;

    @Override
    public void prePersist() {
        super.prePersist();
        if(isRunner == null){
            isRunner = false;
        }
    }
}
