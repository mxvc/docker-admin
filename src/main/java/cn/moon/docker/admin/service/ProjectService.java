package cn.moon.docker.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.StrUtil;
import cn.moon.base.tool.GitTool;
import cn.moon.docker.admin.BuildParam;
import cn.moon.docker.admin.BuildSuccessEvent;
import cn.moon.docker.admin.dao.ProjectDao;
import cn.moon.docker.admin.entity.*;
import cn.moon.docker.sdk.engine.DefaultCallback;
import cn.moon.docker.sdk.engine.DockerSdkManager;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.model.BuildResponseItem;

import io.tmgg.web.persistence.BaseService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;


@Service
@Slf4j
public class ProjectService extends BaseService<Project> {


    @Resource
    RegistryService registryService;


    @Resource
    HostService hostService;

    @Resource
    DockerSdkManager dockerService;

    @Resource
    GitCredentialService gitCredentialService;

    @Resource
    ProjectDao projectDao;

    @Resource
    BuildLogService buildLogService;


    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    private final Map<String, DefaultCallback> buildThreadMap = new HashMap<>();


    public void stopBuild(String logId) throws IOException {
        DefaultCallback callback = buildThreadMap.remove(logId);
        if (callback != null) {
            callback.close();
        }

        BuildLog buildLog = buildLogService.findOne(logId);

        buildLog.setSuccess(false);
        buildLog.setCompleteTime(new Date());
        buildLog.setTimeSpend(buildLog.getCompleteTime().getTime() - buildLog.getCreateTime().getTime());
        buildLogService.save(buildLog);


    }


    public void checkBuildImage() {
        // 判断有无注册中心
        registryService.checkAndFindDefault();

        // 判断有无主机
        long count = hostService.count();
        Assert.state(count > 0, "请先添加主机");


    }

    public void buildImage(BuildParam p) throws IOException {
        List<BuildLog> processing = buildLogService.findByProjectProcessing(p.getProjectId());

        for (BuildLog buildLog : processing) {
            stopBuild(buildLog.getId());
        }

        new Thread(() -> buildImageJob(p)).start();
    }


    public void buildImageJob(BuildParam p) {
        String version = p.getVersion();
        String projectId = p.getProjectId();
        String context = p.getContext();
        String branchOrTag = p.getBranchOrTag();
        String dockerfile = p.getDockerfile();


        Project project = projectDao.findOne(projectId);
        BuildLog buildLog = new BuildLog();
        buildLog.setProjectId(project.getId());
        buildLog.setVersion(version);
        buildLog.setProjectName(project.getName());
        buildLog.setDockerfile(project.getDockerfile());
        buildLog.setValue(project.getBranch());
        buildLog = buildLogService.saveLog(buildLog);
        String logId = buildLog.getId();


        MDC.put("logFileId", logId);
        try {

            log.info("开始构建镜像任务, 项目：{}， 仓库：{}， 分支：{}， 版本：{}", project.getName(), project.getGitUrl(), branchOrTag, version);

            Host host = hostService.findOne(p.getBuildHostId());

            Assert.notNull(host, "无构建主机");
            log.info("构建主机信息... 名称：{}, host:{}, 备注:{}", host.getName(), host.getDockerHost(), StrUtil.emptyIfNull(host.getRemark()));

            GitTool.CloneResult cloneResult = gitClone(project);
            File workDir = cloneResult.getDir();
            log.info("代码下载完毕 " + workDir);
            log.info("代码提交信息: {}" , cloneResult.getCodeMessage());
            Date commitTime = cloneResult.getCommitTime();
            log.info("代码提交时间: {}, {}" , DateUtil.formatDateTime(commitTime), DateUtil.formatBetween(commitTime, new Date()));
            log.info("代码文件大小: {}", DataSizeUtil.format(FileUtil.size(workDir)));


            log.info("dockerfile {},  内容如下", dockerfile);




            buildLog.setBuildHostName(host.getName());
            buildLog.setBuildHostId(host.getId());
            buildLog.setCodeMessage(cloneResult.getCodeMessage());
            buildLog = buildLogService.saveLog(buildLog);

            Registry registry = registryService.checkAndFindDefault();
            log.info("注册中心：{}", registry.getFullUrl());
            DockerClient client = dockerService.getClient(host, registry);


            String imageUrl = registry.getUrl() + "/" + registry.getNamespace() + "/" + project.getName();

            Set<String> imageTags = new HashSet<>();
            imageTags.add(imageUrl + ":" + version);
            if(project.getAutoPushLatest() != null && project.getAutoPushLatest()){
                imageTags.add(imageUrl + ":latest" );
            }

            log.info("目标镜像： {}", imageTags);
            Assert.state(!StrUtil.containsBlank(imageUrl), "镜像路径不能包含空格");



            buildLog.setImageUrl(imageUrl);
            File buildDir = new File(workDir, context);

            log.info("向docker发送构建指令");
            DefaultCallback<BuildResponseItem> buildCallback = new DefaultCallback<>(logId);
            buildThreadMap.put(logId, buildCallback);

            log.info("是否拉取基础镜像 withPull {}", p.isPull());
            log.info("是否使用缓存 {}", p.isUseCache());
            File dockerfileFile = new File(buildDir, dockerfile);
            log.info("dockerfile绝对路径: {}", dockerfileFile.getAbsolutePath());
            log.info("是否拉取基础镜像:{}",p.isPull());


            log.info("构建命令执行中...");
            BuildImageCmd buildImageCmd = client.buildImageCmd(buildDir)
                    // 删除构建产生的容器
                    .withForcerm(true)
                    .withPull(p.isPull())
                    .withNetworkMode("host")
                    .withTags(imageTags)
                    .withNoCache(!p.isUseCache())
                    .withDockerfile(dockerfileFile);


            if(StrUtil.isNotEmpty(project.getBuildArg())){
                Map<String, String> buildArgsMap = UriComponentsBuilder.newInstance().query(project.getBuildArg()).build().getQueryParams().toSingleValueMap();
                for (Map.Entry<String, String> e : buildArgsMap.entrySet()) {
                    log.info("构建参数: {}={}",e.getKey(),e.getValue());
                    buildImageCmd.withBuildArg(e.getKey(), e.getValue());
                }
            }



            buildImageCmd.exec(buildCallback).awaitCompletion();
            log.info("构建命令执行完毕");


            // 判断是构建被中途取消，如手动取消，重复构建取消
            if (!buildThreadMap.containsKey(logId)) {
                log.info("构建被取消");
                MDC.remove("logFileId");
                return;
            }
            log.info("镜像构建结束 ");
            buildThreadMap.remove(logId);

            // 推送
            for (String imageTag : imageTags) {
                log.info("推送镜像 {}", imageTag);
                PushImageCmd pushImageCmd = client.pushImageCmd(imageTag);
                pushImageCmd.exec(new DefaultCallback<>(logId)).awaitCompletion();
                log.info("推送镜像结束 {}", imageTag);
            }


            client.close();


            buildLog.setSuccess(true);
            buildLog.setCompleteTime(new Date());
            buildLog.setTimeSpend(buildLog.getCompleteTime().getTime() - buildLog.getCreateTime().getTime());
            buildLog = buildLogService.save(buildLog);
            log.info("已更新构建日志{}", buildLog);


            Map<String, Object> data = BeanUtil.beanToMap(project, "id", "name", "value");

            BuildSuccessEvent event = new BuildSuccessEvent(this);
            event.setData(data);
            event.setBuildLog(buildLog);
            event.setVersion(version);

            applicationEventPublisher.publishEvent(event);
            log.info("抛出构建事件 {}", event.getBuildLog().getProjectName());

            log.info("构建阶段结束");
        } catch (Exception e) {
            log.info("异常 {} {}", e.getClass().getName(), e.getMessage());


            if (e instanceof IllegalArgumentException && e.getMessage() != null && e.getMessage().contains("Dockerfile does not exist")) {
                log.info("请确保项目下至少有一个Dockerfile文件，不论是否指定其他Dockerfile");
            }
            e.printStackTrace();

            buildLog.setSuccess(false);
            buildLog.setCompleteTime(new Date());
            buildLog.setTimeSpend(buildLog.getCompleteTime().getTime() - buildLog.getCreateTime().getTime());
            buildLogService.save(buildLog);
        }

        MDC.remove("logFileId");
    }

    private GitTool.CloneResult gitClone(Project project) throws GitAPIException {
        String username = null;
        String password = null;
        String branch = project.getBranch();
        GitCredential credential = gitCredentialService.findBestByUrl(project.getGitUrl());
        if (credential != null) {
            username = credential.getUsername();
            password = credential.getPassword();
        }

        log.info("代码下载中...");
        GitTool.CloneResult cloneResult = GitTool.clone(project.getGitUrl(), username, password, branch);
        return cloneResult;
    }


    @Transactional
    public void deleteProject(String id) {
        List<BuildLog> logList = buildLogService.findByProject(id);

        for (BuildLog buildLog : logList) {
            buildLogService.deleteById(buildLog.getId());
        }

        projectDao.deleteById(id);
    }



    @Transactional
    public void cleanErrorLog(String projectId) {
        buildLogService.cleanErrorLog(projectId);
    }

    public Page<Project> findAll(String searchText, Pageable pageable) {
        if (StrUtil.isNotEmpty(searchText)) {
            return projectDao.findByNameLike("%" + searchText.trim() + "%", pageable);
        }
        return projectDao.findAll(pageable);
    }

}
