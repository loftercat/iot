package com.fujica.dto;

import com.fujica.util.SnowFlakeIDGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceDown<P> {
    private String id = SnowFlakeIDGenerator.nextString();
    private String sn;
    private String name;
    private String version = "1.0";
    private long timestamp = System.currentTimeMillis();
    private P payload;
}