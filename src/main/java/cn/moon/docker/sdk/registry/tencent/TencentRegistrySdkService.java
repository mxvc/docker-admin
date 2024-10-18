package cn.moon.docker.sdk.registry.tencent;

import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.sdk.registry.ImageVo;
import cn.moon.docker.sdk.registry.RegistrySdkService;

import java.util.List;

/**
 * 腾讯仓库
 */
public class TencentRegistrySdkService implements RegistrySdkService {
    @Override
    public List<ImageVo> findAll(Registry registry) {
        return null;
    }

    @Override
    public List<String> findTagList(Registry registry, String image) {
        return null;
    }
}
