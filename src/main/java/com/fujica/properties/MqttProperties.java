package com.fujica.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * mqtt配置
 *
 * @author 杨伟
 * @date 2024年05月20日 16:05
 */
@Getter
@Setter
@Component
@ConfigurationProperties("offline.mqtt")
public class MqttProperties {
    /**
     * 内部平台连接地址
     */
    private String innerUrl;
    /**
     * 外部E7/E8连接地址
     */
    private String outerUrl;
    /**
     * 平台登录mqtt服务器账号
     */
    private String username;
    /**
     * 平台登录mqtt服务器密码
     */
    private String password;
    /**
     * 启动生产者客户端数量
     */
    private Integer publishClientCount = 10;
    /**
     * 启动消费者客户端数量
     */
    private Integer consumerClientCount = 10;
    /**
     * 订阅主题
     */
    private String subTopics;
    /**
     * webhook配置的token
     */
    private String webhookToken;
}
