package io.github.jiangood.docker.admin.entity;

import io.admin.framework.data.domain.BaseEntity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;
import java.util.Date;

@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "t_deploy_log")
public class DeployLog extends BaseEntity {


    String appId;
    String appName;

    Date completeTime;

    Boolean success;




}
