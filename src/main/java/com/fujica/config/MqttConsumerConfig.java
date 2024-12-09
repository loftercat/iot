package com.fujica.config;

import cn.hutool.core.util.StrUtil;
import com.fujica.helper.MqttUserHelper;
import com.fujica.helper.RocketMQHelper;
import com.fujica.properties.MqttProperties;
import com.fujica.util.SnowFlakeIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class MqttConsumerConfig {

    @Autowired
    private ConstantConfig config;

    /**
     * 客户端对象
     */
    private List<MqttClient> clients = new ArrayList<>();
    @Autowired
    private RocketMQHelper rocketMQHelper;

    @Autowired
    private MqttProviderConfig providerClient;

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

    public void connectBatch() {
        String subTopics = mqttProperties.getSubTopics();
        if (StrUtil.isEmpty(subTopics)) {
            log.info("未配置订阅主题，不创建MQTT消费者");
            return;
        }

        Integer clientCount = mqttProperties.getConsumerClientCount();
        if (clientCount == null || clientCount <= 0) {
            log.info("未配置启动消费者客户端数量，不创建MQTT消费者");
            return;
        }

        MqttClient client = null;
        for (int i = 0; i < clientCount; i++) {
            client = this.connect(null);
            clients.add(client);
        }

        if (clients.size() <= 0) {
            log.info("创建MQTT消费者失败，关闭程序");
            System.exit(1);
        }
    }

    /**
     * 客户端连接服务端
     */
    public MqttClient connect(MqttClient client) {
        try {
            if(client==null){
                //第一次初始化用
                String clientId = "consumer-" + SnowFlakeIDGenerator.generateSnowFlakeId();
                //添加mqtt账号
                mqttUserHelper.addMqttUser(mqttProperties.getUsername(), mqttProperties.getPassword());
                //创建MQTT客户端对象
                client = new MqttClient(mqttProperties.getInnerUrl(), clientId, new MemoryPersistence());
            }else{
                //重连用
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //连接设置
            MqttConnectOptions options = new MqttConnectOptions();
            //是否清空session，设置为false表示服务器会保留客户端的连接记录，客户端重连之后能获取到服务器在客户端断开连接期间推送的消息
            //设置为true表示每次连接到服务端都是以新的身份
            options.setCleanSession(true);
            //设置连接用户名
            options.setUserName(mqttProperties.getUsername());
            //设置连接密码
            options.setPassword(mqttProperties.getPassword().toCharArray());
            //设置超时时间，单位为秒
            options.setConnectionTimeout(100);
            //设置心跳时间 单位为秒，表示服务器每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
            options.setKeepAliveInterval(20);
            //设置遗嘱消息的话题，若客户端和服务器之间的连接意外断开，服务器将发布客户端的遗嘱信息
            options.setWill("willTopic", (client.getClientId() + "与服务器断开连接").getBytes(), 1, false);
            //订阅主题
            String[] subTopics = this.getSubTopics();
            //设置回调
            MqttConsumerCallBack consumerCallBack = new MqttConsumerCallBack(rocketMQHelper,config, providerClient,client,this);
            client.setCallback(consumerCallBack);
            client.connect(options);
            client.subscribe(subTopics);
            log.info("【mqtt消费者】客户端ID:{},主题:{}", client.getClientId(), mqttProperties.getSubTopics());
            return client;
        } catch (MqttException e) {
            log.error("创建消费者连接异常", e);
            return null;
        }
    }
    /**
     * 配置文件从获取订阅主题，多个主题使用;分割
     *
     * @return
     */
    private String[] getSubTopics() {
        if (StrUtil.isEmpty(mqttProperties.getSubTopics())) {
            return null;
        }
        return mqttProperties.getSubTopics().split(";");
    }
}
