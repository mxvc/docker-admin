package io.github.mxvc.docker.admin.entity;

import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.validator.ValidateStartWithLetter;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.converter.ToListConverter;
import io.tmgg.web.persistence.converter.ToMapConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@FieldNameConstants
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_name",columnNames = {"pid","name"}))
public class DockerComposeServiceItem extends BaseEntity {

    @NotNull
    @ValidateStartWithLetter
    @Column(length = 50)
    String name;

    @NotNull
    @Column(length = 32)
    String pid;

    Integer seq;

    @NotNull
    String containerName;

    @Transient
    public String getImageUrl(){
        String[] arr = image.split(":");
        return arr[0];
    }

    @Transient
    public String getImageTag(){
        String[] arr = image.split(":");
        return arr[1];
    }



    // 以下为docker-compose 格式

    // 镜像，如 ubuntu:latest
    String image;

    String networkMode; // host

    Boolean privileged;

    @Convert(converter = ToMapConverter.class)
    Map<String,String> environment ;

    @Lob
    String command ;

    @Convert(converter = ToListConverter.class)
    List<String> ports;

    @Convert(converter = ToListConverter.class)
    List<String> volumes;

    String restart;

    @Convert(converter = ToListConverter.class)
    List<String> extraHosts;





}
