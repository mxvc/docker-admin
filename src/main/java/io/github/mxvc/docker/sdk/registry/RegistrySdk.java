package io.github.mxvc.docker.sdk.registry;


import io.github.mxvc.docker.admin.entity.Registry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface  RegistrySdk {



    Page<ImageVo> imageList(Registry registry, Pageable pageable, String searchText) throws Exception;


    Page<TagVo> tagList(Registry registry, String imageUrl,String searchText, Pageable pageable) throws Exception;


}
