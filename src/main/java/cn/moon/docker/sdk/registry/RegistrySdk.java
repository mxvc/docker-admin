package cn.moon.docker.sdk.registry;


import cn.moon.docker.admin.entity.Registry;
import com.aliyuncs.exceptions.ClientException;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface  RegistrySdk {



    Page<ImageVo> imageList(Registry registry, Pageable pageable, String searchText) throws Exception;


    Page<TagVo> tagList(Registry registry, String imageUrl,String searchText, Pageable pageable) throws Exception;


}
