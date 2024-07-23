package cn.moon.docker.admin;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.util.List;

public class ImageSyncToHostTool {



    public static void syncImageToHost(DockerClient dockerClient, String src, String hubImage) throws InterruptedException {
        if(!hubImage.contains(":")){
            hubImage += ":latest";
        }

        String imageTag = src + "/" + hubImage;
        System.out.println(imageTag);


        if (findByTag(dockerClient, hubImage) != null) {
            System.out.println("已存在");
            return;
        }

        System.out.println("开始拉取镜像：" + imageTag);
        dockerClient.pullImageCmd(imageTag).exec(new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println(item);
            }
        }).awaitCompletion();
        System.out.println("拉取完成");


        Image image = findByTag(dockerClient, imageTag);

        Assert.notNull(image, "主机不存在镜像 " + imageTag);


        String[] arr = hubImage.split(":");
        String repo = arr[0];
        String tag = arr[1];

        dockerClient.tagImageCmd(image.getId(), repo, tag).exec();
        System.out.println("修改标签完成");

    }


    private static Image findByTag(DockerClient dockerClient, String tag) {
        List<Image> list = dockerClient.listImagesCmd().exec();

        return list.stream().filter(t -> ArrayUtil.contains(t.getRepoTags(), tag)).findFirst().orElse(null);
    }


}
