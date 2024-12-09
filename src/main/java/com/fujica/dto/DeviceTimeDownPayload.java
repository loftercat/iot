package com.fujica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author songcao
 * @version 1.0
 * @description: TODO
 * @date 2024/12/6 14:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTimeDownPayload<B> {
    private String type;
    private B body;
}
