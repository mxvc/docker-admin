package io.github.mxvc.docker.admin.entity;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
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

    /**
     *   host:主机模式（同主机IP）
     *   bridge:桥接（虚拟IP，NAT）
     *   none:  无需网络'
     */
    String networkMode;

    Boolean privileged;

    @Lob
    @Convert(converter = ToMapConverter.class)
    Map<String,String> environment ;

    @Lob
    String command ;

    @Convert(converter = ToListConverter.class)
    List<String> ports;

    @Convert(converter = ToListConverter.class)
    List<String> volumes;

    String restart;

    /**
     *
     *     extra_hosts:
     *       - "hostname:ip_address"
     *       - "example.com:1.2.3.4"
     */
    @Convert(converter = ToListConverter.class)
    List<String> extraHosts;



    // 判断配置是否变化
    @Transient
    public boolean isConfigEquals(DockerComposeServiceItem other){
        return ObjUtil.equals(this.image, other.image) &&
               ObjUtil.equals(this.networkMode, other.networkMode) &&
               ObjUtil.equals(this.privileged, other.privileged) &&
               ObjUtil.equal(this.environment, other.environment) &&
               ObjUtil.equals(this.command, other.command) &&
               ObjUtil.equal(this.ports, other.ports) &&
               ObjUtil.equal(this.volumes, other.volumes) &&
               ObjUtil.equal(this.restart, other.restart) &&
               ObjUtil.equal(this.extraHosts, other.extraHosts);
    }


}
