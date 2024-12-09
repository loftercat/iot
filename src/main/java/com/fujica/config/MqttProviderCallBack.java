package com.fujica.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MqttProviderCallBack implements MqttCallback {

    private ExtendMqttClient client;

    public MqttProviderCallBack(){

    }

    public MqttProviderCallBack(ExtendMqttClient client) {
        this.client = client;
    }

    /**
     * 与服务器断开的回调
     */
    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
        log.info("mqtt生产者={}与服务器断开连接={}",client.getClientId(),cause.getMessage());
        while (!client.isConnected()){
            try {
                log.info("mqtt生产者重连={}",client.getClientId());
                client.reconnect();
                Thread.sleep(30000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 消息到达的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
    }

    /**
     * 消息发布成功的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        IMqttAsyncClient mqttAsyncClient = token.getClient();

        log.info("mqtt发送消息成功-clientId={},messageId={}",mqttAsyncClient.getClientId(),token.getMessageId());


    }
}

