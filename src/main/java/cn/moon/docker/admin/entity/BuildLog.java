package cn.moon.docker.admin.entity;

import cn.moon.lang.web.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Getter
@Setter
@FieldNameConstants
@ToString
public class BuildLog extends BaseEntity {

    @Lob
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
