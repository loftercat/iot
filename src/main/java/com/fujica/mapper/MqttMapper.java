package com.fujica.mapper;

import com.fujica.entity.MqttUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MqttMapper {
    int insertUser(MqttUser record);

    MqttUser selectByUserName(String username);

}
