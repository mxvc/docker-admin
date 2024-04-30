package cn.moon.docker.admin.service;

import cn.moon.docker.admin.dao.RegistryDao;
import cn.moon.docker.admin.entity.Registry;
import cn.moon.lang.web.persistence.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RegistryService extends BaseService<Registry> {

    @Resource
    private RegistryDao dao;




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
