package io.github.mxvc.docker.admin.entity;

import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Getter
@Setter
@FieldNameConstants
public class DockerComposeFileBind extends BaseEntity {


    @NotNull
    String pid;

    @NotNull
    String serviceName;

    @Lob
    String content;


    String targetPath;


    String command;


}
