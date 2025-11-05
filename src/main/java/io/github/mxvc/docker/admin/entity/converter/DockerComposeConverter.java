package io.github.mxvc.docker.admin.entity.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.yaml.YamlUtil;
import io.github.mxvc.base.tool.YamlTool;
import io.github.mxvc.docker.admin.entity.App;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.tmgg.jackson.JsonTool;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class DockerComposeConverter {

    public static final String TAB = "  ";
    public static final String TAB2 = TAB + TAB;
    public static final String TAB3 = TAB + TAB + TAB;
    public static final String LF = "\n";
    public static final String COLON = ":";
    public static final String COL_SPACE = COLON + " ";

    public static List<DockerComposeServiceItem> parse(String content) throws IOException {
        Yaml yaml = new Yaml(new Constructor(Map.class, new LoaderOptions()));

        // 解析YAML文件
        Map<String, Object> composeConfig = yaml.load(content);

        Map<String, Object> services = (Map<String, Object>) composeConfig.get("services");

        List<DockerComposeServiceItem> items = new ArrayList<>();
        for (Map.Entry<String, Object> entry : services.entrySet()) {
            String k = entry.getKey();
            Map<String, Object> v = (Map<String, Object>) entry.getValue();

            String json = JsonTool.toJson(v);
            DockerComposeServiceItem item = JsonTool.jsonToBean(json, DockerComposeServiceItem.class);

            item.setName(k);
            items.add(item);
        }

        return items;
    }

    public static String toConfigFile(List<DockerComposeServiceItem> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("services").append(COLON).append(LF);

        for (DockerComposeServiceItem item : list) {
            if (item.getNetworkMode() != null && !item.getNetworkMode().equals("bridge")) {
                throw new IllegalStateException("暂不支持");
            }

            sb.append(TAB).append(item.getName()).append(COLON).append(LF);
            sb.append(TAB2).append("image").append(COL_SPACE).append(item.getImage()).append(LF);

            if (item.getPrivileged() != null) {
                sb.append(TAB2).append("privileged").append(COL_SPACE).append(item.getPrivileged()).append(LF);
            }
            Map<String, String> environment = item.getEnvironment();
            if (CollUtil.isNotEmpty(environment)) {
                sb.append(TAB2).append("environment").append(COLON).append(LF);
                environment = new TreeMap<>(environment); // key按自然顺序
                for (Map.Entry<String, String> e : environment.entrySet()) {
                    sb.append(TAB3).append(e.getKey()).append(COL_SPACE).append(e.getValue()).append(LF);
                }
            }
            String command = item.getCommand();
            if (StrUtil.isNotEmpty(command)) {
                sb.append(TAB2).append("command").append(COL_SPACE).append(command).append(LF);

            }
            List<String> ports = item.getPorts();
            if (CollUtil.isNotEmpty(ports)) {
                sb.append(TAB2).append("ports").append(COLON).append(LF);
                for (String port : ports) {
                    sb.append(TAB3).append("- ").append(port).append(LF);
                }
            }

            List<String> volumes = item.getVolumes();
            if (CollUtil.isNotEmpty(volumes)) {
                sb.append(TAB2).append("volumes").append(COLON).append(LF);
                for (String v : volumes) {
                    sb.append(TAB3).append("- ").append(v).append(LF);
                }
            }
        }


        return sb.toString();
    }


    public static App convert(DockerComposeServiceItem item) {
                Assert.notNull(item,"item不能为空");

            App app = new App();
            app.setName(item.getName());

            // 解析镜像名称和标签，例如 "nginx:latest" -> imageUrl = "nginx", imageTag = "latest"
            String image = item.getImage();
            if (StrUtil.isNotBlank(image)) {
                String[] parts = image.split(":", 2);
                if (parts.length >= 1) {
                    app.setImageUrl(parts[0]);
                }
                if (parts.length == 2) {
                    app.setImageTag(parts[1]);
                }
            }

            // 构建 AppConfig
            App.AppConfig config = new App.AppConfig();
            app.setConfig(config);

            // Privileged
            if (item.getPrivileged() != null && item.getPrivileged()) {
                config.setPrivileged(true);
            }

            // Environment (假设是 Map<String, String>，我们要转回 YAML 字符串)
            Map<String, String> environment = item.getEnvironment();
            if (environment != null && !environment.isEmpty()) {
                // 简单实现：直接转为 YAML。如果希望格式更可控，可使用 YamlUtil 或其它库
                // 注意：这里简单使用 Map → YAML，你也可以优化格式
                StringWriter writer = new StringWriter();
                YamlUtil.dump(environment, writer);
                config.setEnvironmentYAML(writer.toString());
            }

            // Command
            if (StrUtil.isNotBlank(item.getCommand())) {
                config.setCmd(item.getCommand());
            }

            // Ports
            List<String> ports = item.getPorts();
            if (CollUtil.isNotEmpty(ports)) {
                List<App.PortBinding> portBindings = new ArrayList<>();
                for (String portMapping : ports) {
                    String[] parts = portMapping.split(":", 2);
                    if (parts.length == 2) {
                            int publicPort = Integer.parseInt(parts[0]);
                            int privatePort = Integer.parseInt(parts[1]);
                            App.PortBinding pb = new App.PortBinding();
                            pb.setPublicPort(publicPort);
                            pb.setPrivatePort(privatePort);
                            portBindings.add(pb);

                    }
                }
                config.setPorts(portBindings);
            }

            // Volumes => Binds
            List<String> volumes = item.getVolumes();
            if (CollUtil.isNotEmpty(volumes)) {
                List<App.BindConfig> bindConfigs = new ArrayList<>();
                for (String volumeMapping : volumes) {
                    String[] parts = volumeMapping.split(":", 2);
                    if (parts.length == 2) {
                        App.BindConfig bc = new App.BindConfig();
                        bc.setPublicVolume(parts[0]);
                        bc.setPrivateVolume(parts[1]);
                        bindConfigs.add(bc);
                    }
                }
                config.setBinds(bindConfigs);
            }

            // NetworkMode
            config.setNetworkMode(item.getNetworkMode());


            return app;
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

        List<DockerComposeServiceItem> items = parse(str);

        for (DockerComposeServiceItem item : items) {
            System.out.println("----------------------------------------------");

            App app = convert(item);
            System.out.println(ReflectionToStringBuilder.toString(app));
        }


    }
}
