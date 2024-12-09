package com.fujica.enums;

import lombok.Getter;

@Getter
public enum MqttWebhookTypeEnum {

    /**
     * 连接完成事件
     */
    EVENT_CONNECTED("client.connected"),

    /**
     * 断开连接事件
     */
    EVENT_DISCONNECTED("client.disconnected"),
    ;

    private String event;

    MqttWebhookTypeEnum(String event) {
        this.event = event;
    }
}
