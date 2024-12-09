package com.fujica.config;

import com.fujica.helper.MqttUserHelper;
import com.fujica.properties.MqttProperties;
import com.fujica.util.SnowFlakeIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Configuration
@Slf4j
public class MqttProviderConfig {
    /**
     * 客户端对象
     */
    private List<ExtendMqttClient> clients = new ArrayList<>();
    /**
     * 获取客户端次数
     */
    private AtomicInteger switchClientCount = new AtomicInteger(0);

    @Autowired
    private MqttUserHelper mqttUserHelper;

    @Autowired
    private MqttProperties mqttProperties;

    /**
     * 在bean初始化后连接到服务器
     */
    @PostConstruct
    public void init() {
        this.connectBatch();
    }

    /**
     * 创建多个客户端
     */
    public void connectBatch() {
        Integer clientCount = mqttProperties.getPublishClientCount();
        if (clientCount == null || clientCount <= 0) {
            log.info("未配置启动生产者客户端数量，不创建MQTT消费者");
            return;
        }

        ExtendMqttClient client = null;
        for (int i = 0; i < clientCount; i++) {
            client = this.connect();
            if (client != null) {
                clients.add(client);
            }
        }

        if (clients.size() <= 0) {
            log.info("创建MQTT生产者失败，关闭程序");
            System.exit(1);
        }
    }


    /**
     * 客户端连接服务端
     */
    public ExtendMqttClient connect() {
        try {
            String clientId = "provider-" + SnowFlakeIDGenerator.generateSnowFlakeId();
            //添加mqtt账号
            mqttUserHelper.addMqttUser(mqttProperties.getUsername(), mqttProperties.getPassword());
            //创建MQTT客户端对象
            ExtendMqttClient client = new ExtendMqttClient(mqttProperties.getInnerUrl(), clientId, new MemoryPersistence());
            //连接设置
            MqttConnectOptions options = new MqttConnectOptions();
            //是否清空session，设置false表示服务器会保留客户端的连接记录（订阅主题，qos）,客户端重连之后能获取到服务器在客户端断开连接期间推送的消息
            //设置为true表示每次连接服务器都是以新的身份
            options.setCleanSession(true);
            //设置连接用户名
            options.setUserName(mqttProperties.getUsername());
            //设置连接密码
            options.setPassword(mqttProperties.getPassword().toCharArray());
            //设置超时时间，单位为秒
            options.setConnectionTimeout(100);
            //设置心跳时间 单位为秒，表示服务器每隔 1.5*20秒的时间向客户端发送心跳判断客户端是否在线
            options.setKeepAliveInterval(20);
            //设置遗嘱消息的话题，若客户端和服务器之间的连接意外断开，服务器将发布客户端的遗嘱信息
            options.setWill("willTopic", (clientId + "与服务器断开连接").getBytes(), 0, false);
            //设置回调
            client.setCallback(new MqttProviderCallBack(client));
            client.connect(options);
            log.info("【mqtt生产者】客户端ID:{}", clientId);
            return client;
        } catch (MqttException e) {
            log.error("创建mqtt连接异常", e);
            return null;
        }
    }

    /**
     * 获取客户端
     *
     * @return
     */
    public MqttClient getClient() {
        int index = switchClientCount.getAndUpdate(x -> x >= Integer.MAX_VALUE ? 0 : ++x) % clients.size();
        ExtendMqttClient mqttClient = clients.get(index);

        log.info("client-id:{},in-flight:{}", mqttClient.getClientId(), mqttClient.getInFlightMessageCount());

        //如果in-flight(默认32)满了切换下一个client-id

        return mqttClient;
    }

    public void publish(String topic, String message) {
        log.info("mqtt发送消息topic={}", topic);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(1);
        mqttMessage.setRetained(false);
        mqttMessage.setPayload(message.getBytes());
        //主题的目的地，用于发布/订阅信息
        MqttClient client = this.getClient();
        MqttTopic mqttTopic = client.getTopic(topic);
        //提供一种机制来跟踪消息的传递进度
        //用于在以非阻塞方式（在后台运行）执行发布是跟踪消息的传递进度
        MqttDeliveryToken token;
        try {
            //将指定消息发布到主题，但不等待消息传递完成，返回的token可用于跟踪消息的传递状态
            //一旦此方法干净地返回，消息就已被客户端接受发布，当连接可用，将在后台完成消息传递。
            token = mqttTopic.publish(mqttMessage);
            token.waitForCompletion();
        } catch (MqttException e) {
            log.error("发送消息异常", e);
        }
    }
}