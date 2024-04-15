package cn.moon.docker.admin.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.URLUtil;
import cn.moon.docker.admin.entity.BuildLog;
import cn.moon.docker.admin.service.BuildLogService;
import cn.moon.lang.web.persistence.BaseEntity;
import cn.moon.lang.web.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static cn.moon.docker.admin.controller.LogUrlTool.getLogViewUrl;

@RestController
@Slf4j
@RequestMapping(value = "api/buildLog")
public class BuildLogController {

    @Resource
    private BuildLogService service;

    @RequestMapping("list")
    public Page<BuildLog> list(String projectId, @PageableDefault(sort = BaseEntity.Fields.createTime, direction = Sort.Direction.DESC) Pageable pageable) throws UnsupportedEncodingException {
        Query<BuildLog> q = new Query<>();
        q.eq("projectId", projectId);
        Page<BuildLog> page = service.findAll(q, pageable);


        for (BuildLog log : page) {
            log.setLogUrl(getLogViewUrl(log.getId()));
            if (log.getTimeSpend() == null) {
                log.setTimeSpend(DateUtil.date().getTime() - log.getCreateTime().getTime());
            }
        }

        return page;
    }



}
