package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.service.HostService;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.service.ContainerService;
import cn.moon.docker.sdk.DockerSdkManager;
import cn.moon.lang.json.JsonTool;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.ServletTool;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@Slf4j
@RequestMapping(value = "api/container")
public class ContainerController {

    @Resource
    HostService hostService;

    @Resource
    private DockerSdkManager dockerManager;

    @Resource
    private ContainerService containerService;




    @RequestMapping("log/{hostId}/{containerId}")
    public void logByHost(@PathVariable String hostId, @PathVariable String containerId, HttpServletResponse response) throws Exception {
        DockerClient client = containerService.responseLog(hostId);


        PrintWriter out = response.getWriter();
        client.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .withTail(500)
                .exec(new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        String msg = new String(item.getPayload(), StandardCharsets.ISO_8859_1);
                        out.write(msg);
                        out.flush();
                    }
                }).awaitCompletion();

        System.out.println("日志结束");
    }

    @RequestMapping("downloadLog")
    public void downloadLog(String hostId, String containerId, HttpServletResponse response) throws Exception {
        Host host = hostService.findOne(hostId);

        DockerClient client = dockerManager.getClient(host);


        ServletTool.setDownloadFileHeader(containerId + ".log", response);

        OutputStream out = response.getOutputStream();
        client.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withTimestamps(true)
                .withFollowStream(false)
                .exec(new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        byte[] payload = item.getPayload();
                        try {
                            out.write(payload);
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).awaitCompletion();

        out.close();
        client.close();


    }

    @RequestMapping("downloadFile")
    public void downloadFile(String hostId, String containerId, String file, HttpServletResponse response) throws Exception {
        log.info("进入下载文件");
        Host host = hostService.findOne(hostId);

        DockerClient client = dockerManager.getClient(host);

        InputStream is = client.copyArchiveFromContainerCmd(containerId, file).exec();


        ServletTool.setDownloadFileHeader(FilenameUtils.getName(file) + ".tar", response);

        IoUtil.copy(is, response.getOutputStream());
        is.close();
        client.close();
        log.info("文件下载结束");
    }


    @RequestMapping("remove")
    public Result removeContainer(String hostId, String containerId) throws IOException {
        Host host = hostService.findOne(hostId);
        DockerClient client = dockerManager.getClient(host);

        client.removeContainerCmd(containerId)
                .exec();

        client.close();
        return Result.ok().msg("删除容器成功");

    }

    @RequestMapping("stop")
    public Result stop(String hostId, String containerId) throws IOException {
        Host host = hostService.findOne(hostId);
        DockerClient client = dockerManager.getClient(host);
        client.stopContainerCmd(containerId)
                .exec();
        client.close();
        return Result.ok().msg("停止容器成功");
    }

    @RequestMapping("start")
    public Result start(String hostId, String containerId) throws IOException {
        Host host = hostService.findOne(hostId);

        DockerClient client = dockerManager.getClient(host);

        client.startContainerCmd(containerId)
                .exec();
        client.close();
        return Result.ok().msg("启动容器成功");
    }

    @RequestMapping("status")
    public Result status(String hostId, String appName) {
        Host host = hostService.findOne(hostId);


        DockerClient client = dockerManager.getClient(host);
        Map<String, String> appLabelFilter = dockerManager.getAppLabelFilter(appName);

        try {

            List<Container> list = client.listContainersCmd().withLabelFilter(appLabelFilter).withShowAll(true).exec();
            client.close();
            if (list.isEmpty()) {
                return Result.ok().msg("未知");
            }

            Container container = list.get(0);

            return Result.ok().msg(container.getStatus());
        } catch (Exception e) {

            return Result.ok().msg("未知");
        }

    }


    @RequestMapping("file")
    public Result file(String hostId, String containerId, @RequestParam(defaultValue = "/", required = false) String path) throws Exception {
        Host host = hostService.findOne(hostId);

        DockerClient client = dockerManager.getClient(host);

        ExecCreateCmdResponse response = client.execCreateCmd(containerId)
                .withCmd("sh", "-c", "ls -lt  " + path).withAttachStdout(true).exec();
        String execId = response.getId();


        List<FileVo> dirs = new ArrayList<>();
        List<FileVo> files = new ArrayList<>();


        System.out.println("路径 {}" + path);

        StringBuilder sb = new StringBuilder();
        client.execStartCmd(execId).exec(new ResultCallbackTemplate<ExecStartResultCallback, Frame>() {
            @Override
            public void onNext(Frame frame) {
                String str = new String(frame.getPayload());
                System.out.println(str);
                if (frame.getStreamType() != StreamType.STDOUT) {
                    return;
                }

                sb.append(new String(frame.getPayload()));
            }
        }).awaitCompletion();
        client.close();

        List<String> rs = StrUtil.splitTrim(sb.toString(), "\n");
        if (rs.size() > 0) {
            rs.remove(0);
        }


        for (String line : rs) {
            System.out.println(line);

            String[] parts = line.split("\\s+");


            String name = parts[parts.length - 1];
            String fullPath = path + (path.endsWith("/") ? "" : "/") + name;


            FileVo item = new FileVo();
            item.setDir(parts[0].startsWith("d"));
            item.setTitle(name);
            item.setKey(fullPath.replaceAll("/", "_"));
            item.setPath(fullPath);

            item.size = Long.parseLong(parts[4]);
            item.sizeFmt = DataSizeUtil.format(item.size);

            String time = parts[5] + " " + parts[6] + " " + parts[7];


            item.time = time;


            boolean dir = item.isDir();
            if (dir) {
                dirs.add(item);
            } else {
                files.add(item);
            }
        }
        Map<String, List<FileVo>> data = new HashMap<>();
        data.put("dirs", dirs);
        data.put("files", files);

        return Result.ok().msg("获取文件列表成功").data(data);
    }

    @RequestMapping("cmd")
    public Result cmd(String hostId, String containerId, String cmd) throws Exception {
        Host host = hostService.findOne(hostId);

        DockerClient client = dockerManager.getClient(host);

        ExecCreateCmdResponse response = client.execCreateCmd(containerId)
                .withCmd("sh", "-c", cmd).withAttachStdout(true).exec();
        String execId = response.getId();


        System.out.println("执行命令 {}" + cmd);

        StringBuilder sb = new StringBuilder();
        client.execStartCmd(execId).exec(new ResultCallbackTemplate<ExecStartResultCallback, Frame>() {
            @Override
            public void onNext(Frame frame) {
                String str = new String(frame.getPayload());
                System.out.println(str);
                sb.append(new String(frame.getPayload()));
            }
        }).awaitCompletion();


        client.close();

        return Result.ok().msg("执行命令成功" + cmd).data(sb.toString());
    }


    @Data
    public static class FileVo {
        boolean dir;
        String key;
        String title;

        String path;
        String time;

        long size;
        String sizeFmt;
    }

}
