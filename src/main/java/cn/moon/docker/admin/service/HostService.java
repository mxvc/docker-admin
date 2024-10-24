package cn.moon.docker.admin.service;

import cn.moon.docker.admin.ImageSyncToHostTool;
import cn.moon.docker.admin.dao.HostDao;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.sdk.engine.DockerSdkManager;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.*;
import io.tmgg.lang.dao.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class HostService extends BaseService<Host> {


    @Resource
    DockerSdkManager sdkManager;

    @Resource
    HostDao hostDao;

    /**
     * 获得镜像构建主机
     * @return
     */
    public Host getDefaultDockerRunner(){
        Host host = hostDao.findTop1ByIsRunnerOrderByModifyTimeDesc(true);

        return host;
    }


    public Info getDockerInfo(Host host) {
        DockerClient client = sdkManager.getClient(host);
        Info info = client.infoCmd().exec();

        return info;
    }


    public List<Container> getContainers(String id) {
        Host db = this.findOne(id);
        DockerClient client = sdkManager.getClient(db);

        List<Container> list = client.listContainersCmd().withShowAll(true).exec();
        return list;
    }

    public List<Image> getImages(String id) {
        Host db = this.findOne(id);
        DockerClient client = sdkManager.getClient(db);

        List<Image> list = client.listImagesCmd().withShowAll(true).exec();
        return list;
    }

    public void deleteImage(String hostId, String imageId) {
        Host db = this.findOne(hostId);
        DockerClient client = sdkManager.getClient(db);

        client.removeImageCmd(imageId).exec();

    }


    public long count() {

        return hostDao.count();
    }

    public void syncImageToHost(String hostId, String url, String tag, String newName) throws InterruptedException {
        Host host = this.findOne(hostId);
        DockerClient client = sdkManager.getClient(host);

        ImageSyncToHostTool.syncImageToHost(client, url,tag, newName);
    }

    @Async
    public void cleanImage(String hostId) throws IOException {
        Host db = this.findOne(hostId);
        log.info("开始清理主机镜像 {}", db.getName());
        DockerClient client = sdkManager.getClient(db);
        client.pruneCmd(PruneType.IMAGES)
                .withDangling(false) //  无名镜像
                .exec();
        client.close();
        log.info("清理完成");
    }
}
