package cn.moon.docker.sdk.registry;


import cn.moon.docker.admin.entity.Registry;
import com.aliyuncs.exceptions.ClientException;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface  RegistrySdk {



    Page<ImageVo> imageList(cn.moon.docker.admin.entity.Registry registry, Pageable pageable, String keyword) throws Exception;


    PageImpl<TagVo> tagList(cn.moon.docker.admin.entity.Registry registry, String imageUrl, Pageable pageable) throws Exception;


}
