package com.fujica.aspect.http;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.fujica.constant.HeaderConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author 杨伟
 * @date 2024年04月29日 17:13
 */
@Component
public class JsonRequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * 将application/json request请求包装成自定义的增加类
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        try {
            if (request instanceof HttpServletRequest) {

                //日志追踪
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                String traceId = httpRequest.getHeader(HeaderConstant.TRACE_ID);
                MDC.put(HeaderConstant.TRACE_ID, StrUtil.isNotEmpty(traceId) ? traceId : IdUtil.fastSimpleUUID());
                //用户登录信息
                this.putHeaderDataToMDC(HeaderConstant.USER_TYPE, httpRequest);
                this.putHeaderDataToMDC(HeaderConstant.USER_ID, httpRequest);
                this.putHeaderDataToMDC(HeaderConstant.USERNAME, httpRequest);
                this.putHeaderDataToMDC(HeaderConstant.USER_DETAIL, httpRequest);
                this.putHeaderDataToMDC(HeaderConstant.CLIENT_TYPE, httpRequest);
                this.putHeaderDataToMDC(HeaderConstant.REQUEST_SOURCE, httpRequest);
                if (StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
                    requestWrapper = new JsonRequestWrapper((HttpServletRequest) request, response);
                }
            }
            if (null == requestWrapper) {
                chain.doFilter(request, response);
            } else {
                chain.doFilter(requestWrapper, response);
            }
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void destroy() {

    }

    private void putHeaderDataToMDC(String headerKey, HttpServletRequest request) {
        String headerValue = request.getHeader(headerKey);
        if (StrUtil.isNotEmpty(headerValue)) {
            try {
                headerValue = URLDecoder.decode(headerValue, CharsetUtil.UTF_8);
            } catch (UnsupportedEncodingException e) {
                headerValue = null;
            }
        }

        if (StrUtil.isNotEmpty(headerValue)) {
            MDC.put(headerKey, headerValue);
        }
    }
}
