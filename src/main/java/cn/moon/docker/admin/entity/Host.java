package cn.moon.docker.admin.entity;

import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * 主机信息
 */
@Entity
@Getter
@Setter
@ToString
@FieldNameConstants
public class Host extends BaseEntity {

    @NotNull
    @Column(unique = true)
    String name;
    Boolean isRunner;


    String dockerHost;
    String dockerHostHeader;

    String remark;

    @Override
    public void prePersist() {
        super.prePersist();
        if(isRunner == null){
            isRunner = false;
        }
    }
}
