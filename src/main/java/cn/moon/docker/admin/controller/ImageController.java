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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("image")
public class ImageController  {

    @Resource
    RegistryService registryService;




    @GetMapping({"page"})
    public AjaxResult page(         String registryId, String keyword, @PageableDefault(direction = Sort.Direction.DESC,sort = {"updateTime"}) Pageable pageable) throws Exception {
        Registry registry = StrUtil.isNotEmpty(registryId) ? registryService.findOne(registryId): registryService.checkAndFindDefault();


        RegistrySdk sdk = registryService.findSdkByUrl(registry.getUrl());

        Page<ImageVo> page = sdk.imageList(registry, pageable, keyword);

        return AjaxResult.ok().data(page);
    }



    @GetMapping({"tagPage"})
    public AjaxResult tagPage(  String url, String keyword, Pageable pageable) throws Exception {
        RegistrySdk sdk = registryService.findSdkByUrl(url);
        Registry registry = registryService.findByUrl(url);

        Page<TagVo> page = sdk.tagList(registry, url, keyword,pageable);

        return AjaxResult.ok().data(page);
    }





}

