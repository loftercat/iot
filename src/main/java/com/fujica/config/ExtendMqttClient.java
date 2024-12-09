package com.fujica.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 扩展mqtt客户端
 *
 * @author 杨伟
 * @date 2024年07月17日 14:49
 */
public class ExtendMqttClient extends MqttClient {

    public ExtendMqttClient(String serverURI, String clientId) throws MqttException {
        super(serverURI, clientId);
    }

    public ExtendMqttClient(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
        super(serverURI, clientId, persistence);
    }

    public ExtendMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, ScheduledExecutorService executorService) throws MqttException {
        super(serverURI, clientId, persistence, executorService);
    }

    /**
     * 尚未获得服务端ack的消息数量
     *
     * @return
     */
    public int getInFlightMessageCount() {
        return this.aClient.getInFlightMessageCount();
    }
}
