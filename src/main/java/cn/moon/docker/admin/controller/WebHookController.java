package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.entity.BuildLog;
import cn.moon.docker.admin.entity.Project;
import cn.moon.docker.admin.service.BuildLogService;
import cn.moon.docker.admin.service.ProjectService;
import cn.hutool.core.date.DateUtil;
import cn.moon.lang.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("hook")
public class WebHookController {


    @Resource
    private ProjectService service;


    @RequestMapping("build/{id}")
    public Result build(@PathVariable String id) throws IOException, GitAPIException, InterruptedException {
        String version = "v" + DateUtil.today().replace("-","");

        // 更新最近时间,方便排序
        Project db = service.findOne(id);
        db.setModifyTime(new Date());
        service.save(db);




        service.buildImage(db, db.getBranch(), version, "/", db.getDockerfile(), true);

        return Result.ok().msg("构建命令已发送");
    }


}
