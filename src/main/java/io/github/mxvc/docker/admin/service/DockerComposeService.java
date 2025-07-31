package io.github.mxvc.docker.admin.service;

import io.github.mxvc.docker.admin.dao.DockerComposeDao;
import io.github.mxvc.docker.admin.entity.DockerCompose;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.github.mxvc.docker.admin.entity.converter.DockerComposeConverter;
import io.github.mxvc.docker.sdk.engine.DockerSdkManager;
import io.tmgg.web.persistence.BaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class DockerComposeService extends BaseService<DockerCompose> {

    @Resource
    DockerComposeDao dao;



    @Resource
    DockerComposeServiceItemService itemService;



    @Transactional
    public void saveConfigFile(String id, String content) throws IOException {
        DockerCompose dockerCompose = dao.findOne(id);

        List<DockerComposeServiceItem> items = DockerComposeConverter.parse(content);




        // TODO 先简单粗暴全部删除历史， 有时间在慢慢细化对比，没修改的则不动
        itemService.deleteContainers(dockerCompose);
        itemService.deleteByPid(id);


        for (DockerComposeServiceItem item : items) {
            item.setPid(id);
            item.setContainerName(itemService.getContainerName(dockerCompose, item));
            itemService.save(item);
        }


    }
}

