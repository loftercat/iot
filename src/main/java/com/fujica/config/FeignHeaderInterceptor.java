package com.fujica.config;

import cn.hutool.core.util.ObjectUtil;
import com.fujica.constant.HeaderConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 设置Feign调用请求头
 *
 * @author 杨伟
 * @date 2024年05月25日 9:32
 */
@Configuration
public class FeignHeaderInterceptor implements RequestInterceptor {

    /**
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isEmpty(attributes)) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        //请求头设置用户信息
        requestTemplate.header(HeaderConstant.USER_TYPE,request.getHeader(HeaderConstant.USER_TYPE));
        requestTemplate.header(HeaderConstant.USER_ID,request.getHeader(HeaderConstant.USER_ID));
        requestTemplate.header(HeaderConstant.USERNAME,request.getHeader(HeaderConstant.USERNAME));
        requestTemplate.header(HeaderConstant.USER_DETAIL,request.getHeader(HeaderConstant.USER_DETAIL));
        requestTemplate.header(HeaderConstant.CLIENT_TYPE,request.getHeader(HeaderConstant.CLIENT_TYPE));
        requestTemplate.header(HeaderConstant.TRACE_ID,request.getHeader(HeaderConstant.TRACE_ID) == null ? MDC.get(HeaderConstant.TRACE_ID) : request.getHeader(HeaderConstant.TRACE_ID));
    }


    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    @Primary
    @Scope("prototype")
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
