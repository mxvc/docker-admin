package cn.moon.docker.admin.entity;

import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;


import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import java.util.Date;

@Entity
@Getter
@Setter
@FieldNameConstants
@ToString
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
