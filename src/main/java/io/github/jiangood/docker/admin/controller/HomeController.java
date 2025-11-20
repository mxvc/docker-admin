package io.github.jiangood.docker.admin.controller;

import cn.hutool.core.date.DateUtil;
import io.github.jiangood.docker.admin.entity.BuildLog;
import io.github.jiangood.docker.admin.service.BuildLogService;
import io.admin.common.dto.AjaxResult;
import io.admin.framework.data.query.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("admin/home")
public class HomeController {

    @Resource
    BuildLogService buildLogService;

    @RequestMapping("buildingPage")
    public AjaxResult buildingPage(@PageableDefault(direction = Sort.Direction.DESC,sort = "createTime") Pageable pageable) throws UnsupportedEncodingException {
        JpaQuery<BuildLog> q = new JpaQuery<>();
        q.isNull(BuildLog.Fields.success);

        Page<BuildLog> page = buildLogService.findAll(q,pageable);

        for (BuildLog log : page) {
            log.setLogUrl(LogUrlTool.getLogViewUrl(log.getId()));
            if (log.getTimeSpend() == null) {
                log.setTimeSpend(DateUtil.date().getTime() - log.getCreateTime().getTime());
            }
        }


        return AjaxResult.ok().data(page);

    }
}
