package com.fujica.enums;

import lombok.Getter;

@Getter
public enum MqttOnlineStatusEnum {

    /**
     * 在线
     */
    ONLINE(1),

    /**
     * 离线
     */
    OFFLINE(0),
    ;

    private Integer value;

    MqttOnlineStatusEnum(Integer value) {
        this.value = value;
    }
}
