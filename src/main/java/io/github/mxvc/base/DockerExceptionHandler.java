package io.github.mxvc.base;


import com.github.dockerjava.api.exception.NotFoundException;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.consts.AopSortConstant;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Order(AopSortConstant.GLOBAL_EXP_HANDLER_AOP -1)
@RestControllerAdvice
@Component
public class DockerExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public AjaxResult notFoundException(NoResourceFoundException e) {
        return AjaxResult.err().msg("容器服务或资源不存在");
    }

}
