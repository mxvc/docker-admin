package io.github.mxvc.docker.admin.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.MapTool;
import io.tmgg.lang.validator.ValidateStartWithLetter;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.converter.ToListConverter;
import io.tmgg.web.persistence.converter.ToMapConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.*;

@Entity
@Getter
@Setter
@FieldNameConstants
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_name", columnNames = {"pid", "name"}))
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
    public String getImageUrl() {
        String[] arr = image.split(":");
        return arr[0];
    }

    @Transient
    public String getImageTag() {
        String[] arr = image.split(":");
        return arr[1];
    }


    // 以下为docker-compose 格式

    // 镜像，如 ubuntu:latest
    String image;

    /**
     * host:主机模式（同主机IP）
     * bridge:桥接（虚拟IP，NAT）
     * none:  无需网络'
     */
    String networkMode;

    Boolean privileged;

    @Lob
    @Convert(converter = ToMapConverter.class)
    Map<String, String> environment;

    @Lob
    String command;

    @Convert(converter = ToListConverter.class)
    List<String> ports;

    @Convert(converter = ToListConverter.class)
    List<String> volumes;

    String restart;

    /**
     * extra_hosts:
     * - "hostname:ip_address"
     * - "example.com:1.2.3.4"
     */
    @Convert(converter = ToListConverter.class)
    List<String> extraHosts;


    // 判断配置是否变化
    @Transient
    public boolean isConfigEquals(DockerComposeServiceItem other) {
        Map<String, Object> a = BeanUtil.beanToMap(this);
        Map<String, Object> b = BeanUtil.beanToMap(other);

        Map<String, Object>[] ab = new Map[]{a, b};

        for (Map<String, Object> map : ab) {
            map.remove(Fields.seq);
            map.remove(Fields.pid);
            map.remove(Fields.containerName);
            MapUtil.removeAny(map, BASE_ENTITY_FIELDS);

            // 删除空集合，空字符串
            Iterator<?> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iterator.next();
                Object value = entry.getValue();
                if (StrUtil.isBlankIfStr(value)) {
                    iterator.remove();
                    continue;
                }
                if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                    iterator.remove();
                }
            }
        }

        return a.equals(b);
    }


}
