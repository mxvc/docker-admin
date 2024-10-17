package cn.moon.docker.admin.entity;

import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;
import java.util.Date;

@Entity
@Getter
@Setter
@FieldNameConstants
public class DeployLog extends BaseEntity {


    String appId;
    String appName;

    Date completeTime;

    Boolean success;




}
