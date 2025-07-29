package io.github.mxvc.docker.sdk.registry.tencent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.github.mxvc.docker.admin.entity.Registry;
import io.github.mxvc.docker.sdk.registry.ImageVo;
import io.github.mxvc.docker.sdk.registry.RegistrySdk;
import io.github.mxvc.docker.sdk.registry.TagVo;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.tcr.v20190924.TcrClient;
import com.tencentcloudapi.tcr.v20190924.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// https://cloud.tencent.com/document/sdk 容器镜像服务 (TCR)

@Slf4j
@Component
public class TencentSdk implements RegistrySdk {


    @Override
    public Page<ImageVo> imageList(Registry registry, Pageable pageable, String searchText) throws Exception {
        TcrClient client = getClient(registry);

        DescribeRepositoryOwnerPersonalRequest req = new DescribeRepositoryOwnerPersonalRequest();
        req.setOffset(pageable.getOffset());
        req.setLimit((long) pageable.getPageSize());
        if(StrUtil.isNotEmpty(searchText)){
            req.setRepoName(searchText);
        }



        DescribeRepositoryOwnerPersonalResponse resp = client.DescribeRepositoryOwnerPersonal(req);

        RepoInfoResp data = resp.getData();

        List<ImageVo> voList = new ArrayList<>();
        for (RepoInfo info : data.getRepoInfo()) {
            ImageVo vo = new ImageVo();
            vo.setName( StrUtil.subAfter(info.getRepoName(), "/", true) );
            vo.setUpdateTime(DateUtil.parseDateTime(info.getUpdateTime()));
            vo.setCreateTime(DateUtil.parseDateTime(info.getCreationTime()));
            vo.setDescription(info.getDescription());
            vo.setType(info.getRepoType());
            vo.setTagCount(info.getTagCount());
            vo.setUrl(registry.getUrl() + "/" + info.getRepoName());
            vo.setPullCount(info.getPullCount());

            voList.add(vo);
        }

        PageImpl<ImageVo> page = new PageImpl<>(voList, pageable, data.getTotalCount());

        return page;
    }


    @Override
    public Page<TagVo> tagList(Registry registry, String imageUrl, String searchText, Pageable pageable) throws Exception {
        String repoName = imageUrl.replace(registry.getUrl(), "");
        repoName = StrUtil.removePrefix(repoName,"/");

        TcrClient client = getClient(registry);

        DescribeImagePersonalRequest req = new DescribeImagePersonalRequest();
        req.setRepoName(repoName);
        req.setOffset(pageable.getOffset());
        req.setLimit((long) pageable.getPageSize());
        if(StrUtil.isNotEmpty(searchText)){
            req.setTag(searchText);
        }


        DescribeImagePersonalResponse resp = client.DescribeImagePersonal(req);

        TagInfoResp data = resp.getData();

        List<TagVo> voList = new ArrayList<>();
        for (TagInfo info : data.getTagInfo()) {
            TagVo vo = new TagVo();
            vo.setTagName(info.getTagName());
            vo.setTime(DateUtil.parseDateTime(info.getUpdateTime()));

            voList.add(vo);
        }

        PageImpl<TagVo> page = new PageImpl<>(voList, pageable, data.getTagCount());

        return page;
    }
    private static TcrClient getClient(Registry registry) {
        Credential cred = new Credential(registry.getAk(), registry.getSk());
        TcrClient client = new TcrClient(cred, registry.getRegion());
        return client;
    }


}
