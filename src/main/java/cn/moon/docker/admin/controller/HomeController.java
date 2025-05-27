package cn.moon.docker.admin.controller;

import cn.hutool.core.date.DateUtil;
import cn.moon.docker.admin.entity.BuildLog;
import cn.moon.docker.admin.service.BuildLogService;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import static cn.moon.docker.admin.controller.LogUrlTool.getLogViewUrl;

@RestController
@RequestMapping("home")
public class HomeController {

    @Resource
    BuildLogService buildLogService;

    @PostMapping("buildingPage")
    public AjaxResult buildingPage(@PageableDefault(direction = Sort.Direction.DESC,sort = "createTime") Pageable pageable) throws UnsupportedEncodingException {
        JpaQuery<BuildLog> q = new JpaQuery<>();
        q.isNull(BuildLog.Fields.success);

        Page<BuildLog> page = buildLogService.findAll(q,pageable);

        for (BuildLog log : page) {
            log.setLogUrl(getLogViewUrl(log.getId()));
            if (log.getTimeSpend() == null) {
                log.setTimeSpend(DateUtil.date().getTime() - log.getCreateTime().getTime());
            }
        }


        return AjaxResult.ok().data(page);

    }
}
