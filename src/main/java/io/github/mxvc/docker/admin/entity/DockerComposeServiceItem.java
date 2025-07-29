package io.github.mxvc.docker.admin.entity;

import io.tmgg.jackson.JsonTool;
import lombok.Data;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class DockerComposeServiceItem {

    String name;

    String image;

    String networkMode; // host

    Boolean privileged;

    Map<String,String> environment ;

    String command ;

    List<String> ports;

    List<String> volumes;

    String restart;

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

    public static void main(String[] args) throws IOException {
        String str = """
                services:
                  mysql:
                    image: registry.cn-hangzhou.aliyuncs.com/mxvc/mysql:5.7.35
                    privileged: true
                    environment:
                      MYSQL_ROOT_PASSWORD: 123456
                      MYSQL_DATABASE: ykt_hlt
                      TZ: Asia/Shanghai
                    command:
                      --lower_case_table_names=2
                      --max_connections=1000
                      --character-set-server=utf8mb4
                      --collation-server=utf8mb4_general_ci
                      --wait_timeout=31536000
                      --interactive_timeout=31536000
                      --default-authentication-plugin=mysql_native_password
                      --max_allowed_packet=100M
                      --transaction-isolation=READ-COMMITTED
                    ports:
                      - 3309:3306
                    volumes:
                      - ./mysql_data:/var/lib/mysql
                  redis:
                    image: registry.cn-hangzhou.aliyuncs.com/mxvc/redis:latest
                    ports:
                      - 6379:6379
                """;
        System.out.println(load(str));
    }
}
