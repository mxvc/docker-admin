package io.github.mxvc.docker.admin.entity;

import io.tmgg.jackson.JsonTool;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.converter.ToListConverter;
import io.tmgg.web.persistence.converter.ToMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
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
public class DockerComposeServiceItem extends BaseEntity {

    @Column(length = 50)
    String name;

    @Column(length = 32)
    String dockerComposeId;


    // 以下为docker-compose 格式
    String image;

    String networkMode; // host

    Boolean privileged;

    @Convert(converter = ToMapConverter.class)
    Map<String,String> environment ;

    String command ;

    @Convert(converter = ToListConverter.class)
    List<String> ports;

    @Convert(converter = ToListConverter.class)
    List<String> volumes;

    String restart;

    @Convert(converter = ToListConverter.class)
    List<String> extraHosts;

    public static List<DockerComposeServiceItem> load(String content) throws IOException {
        Yaml yaml = new Yaml(new Constructor(Map.class, new LoaderOptions()));

        // 解析YAML文件
        Map<String, Object> composeConfig = yaml.load(content);

        Map<String,Object> services = (Map<String, Object>) composeConfig.get("services");

        List<DockerComposeServiceItem> items = new ArrayList<>();
        for (Map.Entry<String, Object> entry : services.entrySet()) {
            String k = entry.getKey();
            Map<String,Object> v = (Map<String, Object>) entry.getValue();

            String json = JsonTool.toJson(v);
            DockerComposeServiceItem item = JsonTool.jsonToBean(json, DockerComposeServiceItem.class);

            item.setName(k);
            items.add(item);
        }

        return items;
    }


}
