package com.fujica.controller;
import com.alibaba.fastjson.JSON;
import com.fujica.config.MqttProviderConfig;
import com.fujica.dto.DeviceDown;
import com.fujica.dto.DeviceTimeDownPayload;
import com.fujica.dto.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Resource
    private MqttProviderConfig providerConfig;

    @GetMapping("/ivs_trigger")
    public R<String> hello() {
        String topic = "device/7dd32076-d1203afa/message/down/get_device_timestamp";
        DeviceDown deviceDown = DeviceDown.builder()
                .id("no1")
                .sn("7dd32076-d1203afa")
                .name("get_device_timestamp")
                .version("1.0")
                .payload(DeviceTimeDownPayload.builder().type("get_device_timestamp").build())
                .build();
        String message = JSON.toJSONString(deviceDown);
        log.info("send message: {}", message);
        providerConfig.publish(topic, message);
        return R.ok();
    }
}
