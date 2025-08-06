package io.github.mxvc.docker.admin.entity.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.github.mxvc.base.tool.YamlTool;
import io.github.mxvc.docker.admin.entity.App;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.tmgg.jackson.JsonTool;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
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
                sb.append(TAB2).append("command").append(COLON).append(LF);
                List<String> arr = StrUtil.splitTrim(command, " ");
                for (String a : arr) {
                    sb.append(TAB3).append(a).append(LF);
                }
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

    public static DockerComposeServiceItem convert(App app) {
        StringBuilder sb = new StringBuilder();
        DockerComposeServiceItem item = new DockerComposeServiceItem();
        item.setName(app.getName());
        item.setImage(app.getImageUrl() + ":" + app.getImageTag());
        App.AppConfig config = app.getConfig();


        if (config.isPrivileged()) {
            item.setPrivileged(true);
        }
        String environmentYAML = config.getEnvironmentYAML();
        if (StrUtil.isNotBlank(environmentYAML)) {
            Map<String, Object> dict = YamlTool.yamlToFlattenedMap(environmentYAML);
            Map<String, String> env = new HashMap<>();
            for (Map.Entry<String, Object> e : dict.entrySet()) {
                env.put(e.getKey(), (String) e.getValue());
            }
            item.setEnvironment(env);
        }


        if (StrUtil.isNotEmpty(config.getCmd())) {
            item.setCommand(config.getCmd());

        }
        List<App.PortBinding> ports = config.getPorts();
        if (CollUtil.isNotEmpty(ports)) {
            item.setPorts(new ArrayList<>());
            for (App.PortBinding port : ports) {
                item.getPorts().add(port.getPublicPort() + ":" + port.getPrivatePort());
            }
        }

        List<App.BindConfig> binds = config.getBinds();
        if (CollUtil.isNotEmpty(binds)) {
            item.setVolumes(new ArrayList<>());
            for (App.BindConfig bind : binds) {
                item.getVolumes().add(bind.getPublicVolume() + ":" + bind.getPrivateVolume());
            }
        }


        item.setNetworkMode(config.getNetworkMode());

        if (config.getExtraHosts() != null) {
            Assert.state(false, "开发中。。。");
        }

        return item;

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

        String rs = toConfigFile(items);
        System.out.println(rs);


        System.out.println(str.equals(rs));
    }
}
