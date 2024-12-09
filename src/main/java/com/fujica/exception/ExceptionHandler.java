package com.fujica.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fujica.dto.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.InputStream;
import java.util.stream.Collectors;

/**
 * @author huzw
 * @description:
 * @date 2021/12/07
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    private R handle(HttpMessageNotReadableException e) {
        log.error("全局异常捕获：{}", e.toString());
        if (e.getCause() instanceof JsonParseException) {
            log.error("JSON解析异常：{}", e.getCause().toString());
        } else {
            try (InputStream in = e.getHttpInputMessage().getBody()) {
                byte[] content = new byte[4096];
                int length = in.read(content);
                if (length > 0) {
                    log.error("请求参数无法解析：{}", new String(content, 0, length));
                } else {
                    log.error("请求参数缺失");
                    return R.error(ExceptionCode.SYS_ERROR);
                }
            } catch (Exception exception) {
            }
        }
        return R.error(ExceptionCode.SYS_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private R<Void> handle(HttpRequestMethodNotSupportedException e) {
        log.error("请求为{}方法，而本接口仅支持{}方法", e.getMethod(), e.getSupportedMethods(), e);
        return R.error(ExceptionCode.SYS_ERROR, "接口不存在！");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    private R<Void> handle(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().peek(System.out::println)
                .map(ObjectError::getDefaultMessage).collect(Collectors.joining());
        return R.error(ExceptionCode.COMMON_PARAM_INVALID, message);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataAccessException.class)
    private R<Void> handle(DataAccessException e) {
        log.error("数据库访问异常：{}", e.getClass(), e);
        // 出于安全考虑，所有数据库异常均不返回具体信息到前端
        return R.error(ExceptionCode.SYS_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ServiceException.class)
    private R<Void> handle(ServiceException e) {
        R<Void> response = R.error(e);
        log.info("响应：{}", response);
        return response;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
    private R<String> handle(Throwable e) {
        log.error(e.getMessage(), e);
        //自定义异常特殊处理
        if (e instanceof ServiceException) {
            return R.error(((ServiceException) e).getCode(), e.getMessage());
        } else {
            return R.error(ExceptionCode.SYS_ERROR, e.getMessage());
        }
    }

}
