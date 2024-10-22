package cn.moon.docker.admin.controller;

import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.admin.service.RegistryService;
import cn.moon.docker.sdk.registry.ImageVo;
import cn.moon.docker.sdk.registry.RegistrySdk;
import cn.moon.docker.sdk.registry.TagVo;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.*;
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



    @PostMapping({"tagPage"})
    public AjaxResult tagPage(@RequestBody TagPageParam param, String keyword, Pageable pageable) throws Exception {
        String url = param.getUrl();
        RegistrySdk sdk = registryService.findSdkByUrl(url);
        Registry registry = registryService.findByUrl(url);

        Page<TagVo> page = sdk.tagList(registry, url, keyword,pageable);

        return AjaxResult.ok().data(page);
    }

    @Data
    public static class PageParam {
        String registryId;
    }

    @Data
    public static class TagPageParam {
        String url;
    }

}

