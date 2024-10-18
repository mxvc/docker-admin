package cn.moon.docker.sdk.registry;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Setter
@Getter
public abstract class RegistrySdk {



    public abstract Page<ImageVo> findRepositoryList(cn.moon.docker.admin.entity.Registry registry, Pageable pageable, String keyword) throws Exception;


    public abstract PageImpl<TagVo> findTagList(cn.moon.docker.admin.entity.Registry registry,String imageUrl, Pageable pageable) throws Exception;


}
