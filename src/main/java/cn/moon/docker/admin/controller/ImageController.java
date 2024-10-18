package cn.moon.docker.admin.controller;

import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.admin.service.RegistryService;
import cn.moon.docker.sdk.registry.ImageVo;
import cn.moon.docker.sdk.registry.RegistrySdk;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("image")
public class ImageController  {

    @Resource
    RegistryService registryService;




    @PostMapping({"page"})
    public AjaxResult page(@RequestBody PageParam param, String keyword, @PageableDefault(direction = Sort.Direction.DESC,sort = {"updateTime"}) Pageable pageable) throws Exception {
        String registryId = param.getRegistryId();
        Registry registry = StrUtil.isNotEmpty(registryId) ? registryService.findOne(registryId): registryService.checkAndFindDefault();


        RegistrySdk sdk = registryService.findSdkByUrl(registry.getUrl());

        Page<ImageVo> page = sdk.imageList(registry, pageable, keyword);

        return AjaxResult.ok().data(page);
    }

    @PostMapping({"options"})
    public AjaxResult options(String url) {
        return AjaxResult.ok().data(null);
    }




    @Data
    public static class PageParam {
        String registryId;
    }

}

