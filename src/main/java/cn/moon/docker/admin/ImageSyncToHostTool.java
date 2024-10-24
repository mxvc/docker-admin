package cn.moon.docker.admin;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSyncToHostTool {


    public static void syncImageToHost(DockerClient dockerClient, String url, String tag, String newName) throws InterruptedException {
        String fullUrl = url + ":" + tag;

        log.info("开始拉取镜像：{}", fullUrl);
        dockerClient.pullImageCmd(fullUrl).exec(new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println(item);
            }
        }).awaitCompletion();
        log.info("拉取完成");


        Image image = findByTag(dockerClient, fullUrl);

        Assert.notNull(image, "主机不存在镜像 " + fullUrl);

        if (StrUtil.isEmpty(newName)) {
            return;
        }

        dockerClient.tagImageCmd(image.getId(), newName, tag).exec();
        log.info("修改标签完成 {} >{}", fullUrl, newName);
    }


    private static Image findByTag(DockerClient dockerClient, String tag) {
        List<Image> list = dockerClient.listImagesCmd().exec();

        return list.stream().filter(t -> ArrayUtil.contains(t.getRepoTags(), tag)).findFirst().orElse(null);
    }


}
