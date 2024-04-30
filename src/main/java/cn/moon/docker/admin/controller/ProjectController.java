package cn.moon.docker.admin.controller;

import cn.moon.base.shiro.CurrentUser;
import cn.moon.docker.admin.entity.BuildLog;
import cn.moon.docker.admin.entity.Project;
import cn.moon.docker.admin.service.BuildLogService;
import cn.moon.docker.admin.service.ProjectService;
import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.service.RegistryService;
import cn.moon.lang.web.Option;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.persistence.BaseEntity;
import cn.moon.lang.web.persistence.Query;
import com.aliyuncs.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(value = "api/project")
public class ProjectController {


    @Resource
    private ProjectService service;

    @Resource
    private BuildLogService logService;

    @Resource
    private RegistryService registryService;

    @RequestMapping("check")
    public Result check() {
        long count = registryService.countEnabled();
        if(count == 0){
            return Result.err().msg("请先定义注册中心！");
        }
        return Result.ok();
    }



    @RequiresPermissions("project:list")
    @RequestMapping("list")
    public Page<Project> list(String keyword, @PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Query<Project> q = getQuery();
        q.like(Project.Fields.name, keyword);

        Page<Project> list = service.findAll(q, pageable);
        return list;
    }

    @NotNull
    private static Query<Project> getQuery() {
        Query<Project> q = new Query<>();
        Subject subject = SecurityUtils.getSubject();
        if (!subject.hasRole("admin")) {
            CurrentUser user = (CurrentUser) subject.getPrincipal();
            Set<String> perms = user.getDataPerms().get("P");
            q.in("id", perms);
        }
        return q;
    }

    @RequiresPermissions("project:save")
    @RequestMapping("save")
    public Result save(@RequestBody @Valid Project project) {
        service.saveProject(project);
        return Result.ok().msg("保存成功");
    }

    @RequiresPermissions("project:save")
    @RequestMapping("update")
    public Result update(@RequestBody @Valid Project project) {
        service.saveProject(project);
        return Result.ok().msg("修改成功");
    }


    @RequiresPermissions("project:delete")
    @RequestMapping("delete")
    public Result delete(String id) throws ClientException {
        Project project = service.findOne(id);
        service.deleteProject(id);

        return Result.ok().msg("删除成功");
    }


    @RequiresPermissions("project:list")
    @RequestMapping("get")
    public Project get(String id) {
        return service.findOne(id);
    }


    @RequiresPermissions("project:build")
    @RequestMapping("build")
    public Result build(@RequestParam String projectId,
                        String value,
                        String version,
                        @RequestParam(defaultValue = "/") String context,
                        @RequestParam(defaultValue = "Dockerfile") String dockerfile,
                        @RequestParam(defaultValue = "true") Boolean useCache) throws InterruptedException, IOException, GitAPIException {

        Assert.notNull(value, "请输入branch或tag");
        if (StrUtil.isEmpty(version)) {
            version = value;
        }


        Project project = service.findOne(projectId);
        service.checkBuildImage();


        // 更新最近时间,方便排序
        project.setModifyTime(new Date());
        service.save(project);

        BuildLog buildLog = new BuildLog();
        buildLog.setProjectId(projectId);
        buildLog.setVersion(version);
        buildLog.setProjectName(project.getName());
        buildLog.setDockerfile(dockerfile);
        buildLog.setValue(value);
        buildLog = logService.save(buildLog);

        MDC.put("logFileId", buildLog.getId());
        log.info("控制层收到构建指令，开始异步调用服务");
        service.buildImage(buildLog.getId(), value, version, context, dockerfile, useCache);


        return Result.ok();
    }

    @RequestMapping("stopBuild")
    public Result stopBuild(@RequestParam String id) throws IOException {
        service.stopBuild(id);

        return Result.ok();
    }

    @RequestMapping("cleanErrorLog")
    public Result cleanErrorLog(@RequestParam String id) {
        service.cleanErrorLog(id);
        return Result.ok();
    }


    @RequestMapping("options")
    public List<Option> options() throws InterruptedException, IOException, GitAPIException {
        Query<Project> q = getQuery();

        List<Project> list = service.findAll(q, Sort.by(Sort.Direction.DESC, BaseEntity.Fields.modifyTime));

        List<Option> options = new ArrayList<>();
        for (Project h : list) {
            options.add(Option.valueLabel(h.getId(), h.getName()));
        }


        return options;
    }

    @RequestMapping("versions")
    public List<Option> versions(String projectId) throws InterruptedException, IOException, GitAPIException {
        List<String> versions = logService.versions(projectId);

        List<Option> options = versions.stream().map(v -> Option.valueLabel(v, v)).collect(Collectors.toList());


        return options;
    }
}
