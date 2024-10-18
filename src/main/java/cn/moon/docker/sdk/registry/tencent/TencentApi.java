package cn.moon.docker.sdk.registry.tencent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.moon.docker.sdk.registry.ImageVo;
import cn.moon.docker.sdk.registry.RegistryApi;
import cn.moon.docker.sdk.registry.TagVo;
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

@Slf4j
@Component
public class TencentApi extends RegistryApi {

    @Override
    public Page<ImageVo> findRepositoryList(cn.moon.docker.admin.entity.Registry registry, Pageable pageable, String keyword) throws Exception {
        Credential cred = new Credential(registry.getAk(), registry.getSk());

        TcrClient client = new TcrClient(cred, registry.getRegion());

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

    @Override
    public PageImpl<TagVo> findTagList(cn.moon.docker.admin.entity.Registry registry, String imageUrl, Pageable pageable) throws Exception {
        String repoName = imageUrl.replace(registry.getUrl(), "");
        repoName = StrUtil.removePrefix(repoName,"/");

        Credential cred = new Credential(registry.getAk(), registry.getSk());
        TcrClient client = new TcrClient(cred, registry.getRegion());

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
