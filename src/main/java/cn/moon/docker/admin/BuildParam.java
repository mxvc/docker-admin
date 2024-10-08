package cn.moon.docker.admin;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
public class BuildParam {

    String projectId;

    String branchOrTag = "master";
    String version;
    String context = "/";
    String dockerfile = "Dockerfile";
    boolean useCache = true;

    String buildHostId = "default";

    // 构建时是否拉取最近镜像
    boolean pull = false;

    public Map<String,Object> toMap(){
       return BeanUtil.beanToMap(this);
    }
}
