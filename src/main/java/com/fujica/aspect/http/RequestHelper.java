package com.fujica.aspect.http;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author 杨伟
 * @date 2024年04月29日 17:15
 */
@Slf4j
public class RequestHelper {

    /**
     * body 获取
     *
     * @param request
     * @return {@link String}
     */
    public static String getBody(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (
                InputStream inputStream = request.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error("从输入流中读取参数异常", e);
        }
        return sb.toString();
    }
}
