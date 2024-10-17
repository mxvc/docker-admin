package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.BuildParam;
import cn.moon.docker.admin.entity.BuildLog;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.entity.Project;
import cn.moon.docker.admin.service.BuildLogService;
import cn.moon.docker.admin.service.HostService;
import cn.moon.docker.admin.service.ProjectService;
import cn.hutool.core.date.DateUtil;
import cn.moon.lang.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("hook")
public class WebHookController {


    @Resource
    private ProjectService service;

    @Resource
    HostService hostService;

    @RequestMapping("build/{id}")
    public Result build(@PathVariable String id) throws IOException, GitAPIException, InterruptedException {
        log.info("触发自动构建");
        String version = "v" + DateUtil.today().replace("-","");

        // 更新最近时间,方便排序
        Project db = service.findOne(id);
        db.setModifyTime(new Date());
        service.save(db);


        BuildParam buildParam = new BuildParam();
        buildParam.setVersion(version);
        buildParam.setBranchOrTag(db.getBranch());
        buildParam.setProjectId(db.getId());
        Host host = hostService.getDefaultDockerRunner();
        if(host == null){
            log.error("无构建主机，返回");
            return Result.err().msg("无构建主机，请先添加构建主机");
        }
        buildParam.setBuildHostId(host.getId());
        service.buildImage(buildParam);

        return AjaxResult.ok().msg("构建命令已发送");
    }


}
