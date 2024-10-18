package cn.moon.docker.sdk.registry;

import cn.moon.docker.admin.entity.Registry;

import java.util.List;

public interface RegistrySdkService {


     List<ImageVo> findAll(Registry registry);


    List<String> findTagList(Registry registry, String image);
}
