package cn.moon.docker.admin.entity;

import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;


import java.util.Date;

@Entity
@Getter
@Setter
@FieldNameConstants
@ToString
@Table(name = "t_build_log")
public class BuildLog extends BaseEntity {

    @Lob
    @Column(length = DBConstants.LEN_MAX_VARCHAR)
    String codeMessage;

    String buildHostName;
    String buildHostId;

    String projectName;
    String projectId;

    String imageUrl;

    Date completeTime;

    Boolean success;

    String value;

    String version;


    String context = "/";
    String dockerfile ;


    @Transient
    String logUrl;


    Long timeSpend; // 用时， 毫秒


}
