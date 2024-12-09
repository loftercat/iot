package com.fujica.helper;

import cn.hutool.core.convert.Convert;
import com.fujica.entity.MqttUser;
import com.fujica.mapper.MqttMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Component
public class MqttUserHelper {
    @Autowired
    private MqttMapper mqttMapper;

    /**
     * 添加mqtt账号
     *
     * @param username
     * @return
     */
    public MqttUser addMqttUserByRandomPassword(String username) {
        return this.addMqttUser(username, Convert.toStr(System.currentTimeMillis()));
    }

    /**
     * 添加mqtt账号
     *
     * @param username
     * @param password
     */
    public MqttUser addMqttUser(String username, String password) {
        //将username和加密后的password存储在emqx的redis中
        MqttUser mqttUser = new MqttUser();
        mqttUser.setCreated(new Date());
        mqttUser.setIsSuperuser(1);
        String salt = bytesToHex(generateSalt(16));
        mqttUser.setUsername(username);
        mqttUser.setSalt(salt);
        mqttUser.setPasswordHash(password);
        mqttUser.setRemark(password);

        //是否已经存在
        MqttUser user = mqttMapper.selectByUserName(username);
        if (user != null) {
            return user;
        }

        int effect = mqttMapper.insertUser(mqttUser);
        if (effect > 0) {
            return mqttUser;
        }
        return null;
    }

    private byte[] generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }
}
