package cn.moon.docker.admin.controller;

import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.admin.service.RegistryService;
import cn.moon.docker.sdk.registry.ImageVo;
import cn.moon.docker.sdk.registry.RegistrySdk;
import cn.moon.docker.sdk.registry.TagVo;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("image")
public class ImageController {

    @Resource
    RegistryService registryService;

    @Data
    public static class QueryParam{
        String registryId;
        String keyword;
    }


    @PostMapping("page")
    public AjaxResult page(@RequestBody QueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = {"updateTime"}) Pageable pageable) throws Exception {
        String keyword = param.getKeyword();
        String registryId = param.getRegistryId();
        Registry registry = StrUtil.isNotEmpty(registryId) ? registryService.findOne(registryId) : registryService.checkAndFindDefault();


        RegistrySdk sdk = registryService.findSdkByUrl(registry.getUrl());

        Page<ImageVo> page = sdk.imageList(registry, pageable, keyword);

        return AjaxResult.ok().data(page);
    }


    @GetMapping("tagPage")
    public AjaxResult tagPage(String url, String keyword, Pageable pageable) throws Exception {
        RegistrySdk sdk = registryService.findSdkByUrl(url);
        Registry registry = registryService.findByUrl(url);

        Page<TagVo> page = sdk.tagList(registry, url, keyword, pageable);

        return AjaxResult.ok().data(page);
    }

    @GetMapping("options")
    public AjaxResult options(String registryId, String keyword,
                              @PageableDefault(direction = Sort.Direction.DESC, sort = {"updateTime"}) Pageable pageable) throws Exception {
        Registry registry = StrUtil.isNotEmpty(registryId) ? registryService.findOne(registryId) : registryService.checkAndFindDefault();

        RegistrySdk sdk = registryService.findSdkByUrl(registry.getUrl());

        Page<ImageVo> page = sdk.imageList(registry, pageable, keyword);

        List<Option> options = Option.convertList(page, ImageVo::getUrl, ImageVo::getUrl);
        return AjaxResult.ok().data(options);
    }

    @GetMapping("tagOptions")
    public AjaxResult tagOptions(String url, String keyword, Pageable pageable) throws Exception {
        RegistrySdk sdk = registryService.findSdkByUrl(url);
        Registry registry = registryService.findByUrl(url);
        if(registry == null){
            return AjaxResult.ok().data(Collections.emptyList());
        }

        Page<TagVo> page = sdk.tagList(registry, url, keyword, pageable);
        List<Option> options = Option.convertList(page, TagVo::getTagName, TagVo::getTagName);
        return AjaxResult.ok().data(options);
    }


}

