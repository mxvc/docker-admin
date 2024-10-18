package cn.moon.docker.sdk.registry.tencent;

import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.sdk.registry.ImageVo;
import cn.moon.docker.sdk.registry.RegistrySdk;
import cn.moon.docker.sdk.registry.TagVo;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import io.tmgg.lang.JsonTool;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  阿里云个人版sdk已下线，但是目前还能用
 */
@Slf4j
@Getter
@Setter
public class AliyunSdk implements RegistrySdk {




    public Page<ImageVo> imageList(cn.moon.docker.admin.entity.Registry registry, Pageable pageable, String keyword) throws Exception {
        IAcsClient client = getClient(registry);
        CommonRequest request = getCommonRequest(registry);


        request.setUriPattern("/repos/" + registry.getNamespace());
        String requestBody = "" +
                             "{}";
        request.setHttpContent(requestBody.getBytes(), "utf-8", FormatType.JSON);
        request.putQueryParameter("PageSize", String.valueOf(pageable.getPageSize()));
        request.putQueryParameter("Page", String.valueOf(pageable.getPageNumber() + 1));
        if (keyword != null) {
            request.putQueryParameter("RepoNamePrefix", keyword);
        }
        CommonResponse response = client.getCommonResponse(request);
        String data = response.getData();


        return convertRepository(data, pageable);
    }


    @Override
    public PageImpl<TagVo> tagList(cn.moon.docker.admin.entity.Registry registry, String imageUrl, Pageable pageable) throws ClientException {
        int page = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        CommonRequest request = getCommonRequest(registry);

        String subUrl = imageUrl.substring(imageUrl.indexOf("/") + 1);


        request.setUriPattern("/repos/" + subUrl + "/tags");
        String requestBody = "{}";
        request.setHttpContent(requestBody.getBytes(), "utf-8", FormatType.JSON);

        request.putQueryParameter("PageSize", String.valueOf(pageSize)); // pageSize 必须是大于0的整数并且小于等于100的整数

        request.putQueryParameter("Page", String.valueOf(page)); // 新版本是PageNo, 这里还用Page

        IAcsClient client = getClient(registry);

        CommonResponse response = client.getCommonResponse(request);
        String data = response.getData();


        Map<String, Object> json = JsonTool.jsonToMapQuietly(data);

        Map<String, Object> jsonData = (Map<String, Object>) json.get("data");


        List< Map<String, Object>> tagList = (List<Map<String, Object>>) jsonData.get("tags");


        Integer total = (Integer) jsonData.get("total");

        List<TagVo> imageTagList = tagList.stream().map(tag -> {
            TagVo t = new TagVo();
            t.setTime(new Date((Long) tag.get("imageUpdate")));
            t.setName((String) tag.get("tag"));
            return t;
        }).collect(Collectors.toList());


        return new PageImpl<>(imageTagList, pageable, total);
    }




    private CommonRequest getCommonRequest(Registry registry) {
        String url = registry.getUrl();
        String[] split = url.split("\\.");
        String regionId = split[1];

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.GET);
        request.setDomain("cr." + regionId + ".aliyuncs.com");
        request.setVersion("2016-06-07");
        request.putHeadParameter("Content-Type", "application/json");

        return request;
    }

    private IAcsClient getClient(Registry registry) {
        String host = registry.getUrl();
        String[] split = host.split("\\.");
        String regionId = split[1];

        DefaultProfile profile = DefaultProfile.getProfile(
                regionId,
                registry.getAk(),
              registry.getSk());

        IAcsClient client = new DefaultAcsClient(profile);

        return client;
    }

    private Page<ImageVo> convertRepository(String json, Pageable pageable) throws IOException {

        Map<String, Object> response = JsonTool.jsonToMapQuietly(json);


        Map<String, Object> data = (   Map<String, Object>) response.get("data");

        Integer total = (Integer) data.get("total");

        List<ImageVo> list = new ArrayList<>();
        List<Map<String,Object>> repos = (List<Map<String,Object>>) data.get("repos");
        repos.forEach(aliRepos -> {
            ImageVo r = new ImageVo();
            r.setName((String) aliRepos.get("repoName"));
            r.setSummary((String) aliRepos.get("summary"));
            r.setType((String) aliRepos.get("repoType"));
            r.setTime(new Date((Long) aliRepos.get("gmtModified")));

            Map<String, String> repoDomainList = (Map<String, String>) aliRepos.get("repoDomainList");

            String domain = repoDomainList.get("public");
            Object namespace = aliRepos.get("repoNamespace");
            String url = domain + "/" + namespace + "/" + r.getName();

            r.setUrl(url);


            list.add(r);
        });


        Page<ImageVo> page = new PageImpl<>(list, pageable, total);

        return page;
    }
}
