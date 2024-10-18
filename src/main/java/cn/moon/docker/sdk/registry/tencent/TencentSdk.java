package cn.moon.docker.sdk.registry.tencent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.sdk.registry.ImageVo;
import cn.moon.docker.sdk.registry.RegistrySdk;
import cn.moon.docker.sdk.registry.TagVo;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tcr.v20190924.TcrClient;
import com.tencentcloudapi.tcr.v20190924.models.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TencentSdk implements RegistrySdk {

    @Override
    public List<NamespaceVo> nameList(Registry registry) throws TencentCloudSDKException {
        TcrClient client = getClient(registry);
        DescribeNamespacesRequest req = new DescribeNamespacesRequest();
        DescribeNamespacesResponse resp = client.DescribeNamespaces(req);

        return null;
    }

    @Override
    public Page<ImageVo> imageList(cn.moon.docker.admin.entity.Registry registry, Pageable pageable, String keyword) throws Exception {
        TcrClient client = getClient(registry);

        DescribeRepositoryOwnerPersonalRequest req = new DescribeRepositoryOwnerPersonalRequest();
        DescribeRepositoryOwnerPersonalResponse resp = client.DescribeRepositoryOwnerPersonal(req);

        RepoInfoResp data = resp.getData();

        List<ImageVo> voList = new ArrayList<>();
        for (RepoInfo info : data.getRepoInfo()) {
            ImageVo vo = new ImageVo();
            vo.setName( StrUtil.subAfter(info.getRepoName(), "/", true) );
            vo.setTime(DateUtil.parseDateTime(info.getUpdateTime()));
            vo.setDescription(info.getDescription());
            vo.setType(info.getRepoType());
            vo.setTagCount(info.getTagCount());
            vo.setUrl(registry.getUrl() + "/" + vo.getName());


            voList.add(vo);
        }

        PageImpl<ImageVo> page = new PageImpl<>(voList, pageable, data.getTotalCount());

        return page;
    }

    @NotNull
    private static TcrClient getClient(Registry registry) {
        Credential cred = new Credential(registry.getAk(), registry.getSk());
        TcrClient client = new TcrClient(cred, registry.getRegion());
        return client;
    }

    @Override
    public PageImpl<TagVo> tagList(cn.moon.docker.admin.entity.Registry registry, String imageUrl, Pageable pageable) throws Exception {
        String repoName = imageUrl.replace(registry.getUrl(), "");
        repoName = StrUtil.removePrefix(repoName,"/");

        TcrClient client = getClient(registry);

        DescribeImagePersonalRequest req = new DescribeImagePersonalRequest();
        req.setRepoName(repoName);

        DescribeImagePersonalResponse resp = client.DescribeImagePersonal(req);

        TagInfoResp data = resp.getData();

        List<TagVo> voList = new ArrayList<>();
        for (TagInfo info : data.getTagInfo()) {
            TagVo vo = new TagVo();
            vo.setName(info.getTagName());
            vo.setTime(DateUtil.parseDateTime(info.getUpdateTime()));

            voList.add(vo);
        }

        PageImpl<TagVo> page = new PageImpl<>(voList, pageable, data.getTagCount());

        return page;
    }


}
