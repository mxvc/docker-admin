package io.github.mxvc.docker.admin.service;

import io.github.mxvc.docker.admin.dao.RegistryDao;
import io.github.mxvc.docker.admin.entity.Registry;
import io.github.mxvc.docker.sdk.registry.RegistrySdk;
import io.github.mxvc.docker.sdk.registry.tencent.AliyunSdk;
import io.github.mxvc.docker.sdk.registry.tencent.TencentSdk;

import io.tmgg.web.persistence.BaseService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class RegistryService extends BaseService<Registry> {

    @Resource
    private RegistryDao dao;


    public RegistrySdk findSdkByUrl(String url){
        if(url.contains("aliyun")){
            return new AliyunSdk();
        }
        if(url.contains("tencent")){
            return new TencentSdk();
        }
        return null;
    }



    public Registry findByUrl(String url) {
        List<Registry> list = dao.findAll();

        for (Registry registry : list) {
            if (url.startsWith(registry.getUrl())) {
                return registry;
            }
        }
        return null;
    }

    public Registry checkAndFindDefault() {
        Registry registry = dao.findByDefaultRegistryIsTrue();

        if(registry == null){
            long count = dao.count();
            if(count == 1){
                return dao.findAll().get(0);
            }else {
                throw new IllegalStateException("没有设置默认注册中心");
            }
        }else {
            return registry;
        }
    }


    public List<Registry> findAll() {
        return dao.findAll();
    }

    public long countEnabled() {
        return dao.count();
    }
}
