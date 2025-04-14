package cn.moon.docker.admin.entity;

import cn.moon.docker.admin.entity.converter.AppConfigConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;

import io.tmgg.lang.validator.ValidateStartWithLetter;
import io.tmgg.modules.sys.entity.SysOrg;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.List;

@Msg("应用")
@Entity
@Getter
@Setter
@Table(name = "t_app")
@FieldNameConstants
public class App extends BaseEntity {

    @ValidateStartWithLetter
    @NotNull
    @Column(unique = true)
    String name;

    @ManyToOne
    SysOrg sysOrg;


    @NotNull
    @ManyToOne
    Host host;


    String imageUrl;

    @Column(length = 20)
    String imageTag;


    Boolean autoDeploy;



    @Transient
    String logUrl;


    @Lob
    @Convert(converter = AppConfigConverter.class)
    @Column(length = DBConstants.LEN_MAX_VARCHAR)
    AppConfig config;

    @ManyToOne
    Project project;

    String remark;


    @Override
    public void prePersist() {
        super.prePersist();
        if (autoDeploy == null) {
            autoDeploy = true;
        }
        if (config == null) {
            config = new AppConfig();
            config.setNetworkMode("bridge");
        }

    }


    @Data
    public static class AppConfig {


        String image;
        boolean privileged;


        String cmd; //启动命令

        String extraHosts; // ip映射

        // 主机:容器
        List<PortBinding> ports = new ArrayList<>(); //  - 7100:7100/udp  - 7100:7100/tcp

        /**
         * /var/run/docker.sock:/var/run/docker.sock:ro
         * /var/run/docker.sock:/var/run/docker.sock:rw
         */
        List<BindConfig> binds = new ArrayList<>();


        String environmentYAML;

        /**
         *   host:主机模式（同主机IP）
         *   bridge:桥接（虚拟IP，NAT）
         *   none:  无需网络'
         */
        String networkMode;

        public String getNetworkMode() {
            if (networkMode == null) {
                networkMode = "bridge";
            }
            return networkMode;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class BindConfig {
        String publicVolume;
        String privateVolume;
        Boolean readOnly; // ro, rw

    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class PortBinding {
        Integer publicPort;
        Integer privatePort;
        String protocol;

    }


}
