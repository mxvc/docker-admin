
package cn.moon.base;

import cn.moon.lang.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(UnauthorizedException.class)
    public Result ex(UnauthorizedException e) {
        log.error("异常", e);
        return Result.err().msg(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()).code(403);
    }


}


