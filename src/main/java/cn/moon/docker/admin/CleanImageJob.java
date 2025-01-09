package cn.moon.docker.admin;

import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.service.HostService;
import cn.moon.docker.sdk.engine.DockerSdkManager;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.PruneResponse;
import com.github.dockerjava.api.model.PruneType;
import io.tmgg.modules.job.JobTool;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CleanImageJob implements Job {

    private static final Logger log = JobTool.getLogger();

    @Resource
    HostService hostService;

    @Resource
    DockerSdkManager sdkManager;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("开始清理资源");

        List<Host> list = hostService.findAll();

        for (Host host : list) {

            try {
                log.info("清理服务器 {}", host.getName());

                DockerClient client = sdkManager.getClient(host);


                PruneResponse response = client.pruneCmd(PruneType.IMAGES)
                        .withDangling(false) //  无名镜像
                        .withUntilFilter( (30 * 24) + "h") // 超过x天
                        .exec();
                log.info(" raw values {}", response.getRawValues());
                log.info("{} 回收空间  {}", host.getName(), response.getSpaceReclaimed());

                client.close();

            } catch (Exception e) {
                log.info("清理失败  " + e.getMessage(), e);
            }

        }
    }




}
