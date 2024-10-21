package cn.moon.docker.admin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.sys.org.entity.SysOrg;
import jakarta.persistence.ManyToOne;
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
@Remark("主机")
@Entity
@Getter
@Setter
@ToString
@FieldNameConstants
public class Host extends BaseEntity {

    @Remark("名称")
    @NotNull
    @Column(unique = true)
    String name;

    @Remark("构建节点")
    Boolean isRunner;


    String dockerHost;

    @Remark("请求头Host重写")
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
