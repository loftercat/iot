package com.fujica.dto;
import lombok.Data;

@Data
public class MqMsgDto {
    /**
     * 设备sn
     */
    private String sn;

    /**
     * 业务数据json
     */
    private String  data;

}
