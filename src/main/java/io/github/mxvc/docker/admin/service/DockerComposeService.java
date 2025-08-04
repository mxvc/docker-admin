package io.github.mxvc.docker.admin.service;

import io.github.mxvc.docker.admin.dao.DockerComposeDao;
import io.github.mxvc.docker.admin.entity.App;
import io.github.mxvc.docker.admin.entity.DockerCompose;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.github.mxvc.docker.admin.entity.converter.DockerComposeConverter;
import io.tmgg.web.persistence.BaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DockerComposeService extends BaseService<DockerCompose> {

    @Resource
    DockerComposeDao dao;


    @Resource
    DockerComposeServiceItemService itemService;

    @Resource
    AppService appService;


    @Transactional
    public void saveConfigFile(String id, String content) throws IOException {
        DockerCompose dockerCompose = dao.findOne(id);

        List<DockerComposeServiceItem> items = DockerComposeConverter.parse(content);
        Map<String, DockerComposeServiceItem> itemsMap = items.stream().collect(Collectors.toMap(DockerComposeServiceItem::getName, t -> t));

        List<DockerComposeServiceItem> dbItems = itemService.findByPid(id);


        // 删除数据库中不存在或配置变化的
        for (DockerComposeServiceItem dbItem : dbItems) {
            boolean exist = itemsMap.containsKey(dbItem.getName());
            boolean changed = false;
            if (exist) {
                // 再次判断配置
                DockerComposeServiceItem item = itemsMap.get(dbItem.getName());
                changed = !item.getName().equals(dbItem.getName()) || !dbItem.isConfigEquals(item);
            }
            log.info("检查历史容器 {} 存在{} 配置变化{}", dbItem.getName(), exist, changed);
            if (!exist || changed) {
                itemService.deleteContainer(dockerCompose, dbItem.getId());
                itemService.deleteById(dbItem.getId());
            }
        }

        //
        dbItems = itemService.findByPid(id);
        Map<String, DockerComposeServiceItem> dbItemsMap = dbItems.stream().collect(Collectors.toMap(DockerComposeServiceItem::getName, t -> t));

        for (int i = 0; i < items.size(); i++) {
            DockerComposeServiceItem item = items.get(i);
            if (dbItemsMap.containsKey(item.getName())) {
                DockerComposeServiceItem dbItem = dbItemsMap.get(item.getName());
                dbItem.setSeq(i); // 存在则保存下新的顺序
            } else {
                item.setPid(id);
                item.setSeq(i);
                item.setContainerName(itemService.getContainerName(dockerCompose, item));
                itemService.saveAndFlush(item);
            }
        }

    }

    @Transactional
    public void moveApp(String id, String appId) {
        DockerCompose dockerCompose = dao.findOne(id);
        App app = appService.findOne(appId);
        Assert.state(app.getHost().equals(dockerCompose.getHost()), "注意不一致不能移动");

        DockerComposeServiceItem item = DockerComposeConverter.convert(app);
        item.setContainerName(itemService.getContainerName(dockerCompose, item));
        item.setPid(id);

        itemService.save(item);


        appService.deleteApp(appId);
    }
}

