package com.fujica.config;
import com.alibaba.fastjson.JSON;
import com.fujica.dto.MqMsgDto;
import com.fujica.helper.RocketMQHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
public class MqttConsumerCallBack implements MqttCallback{

    private RocketMQHelper rocketMQHelper;

    private MqttProviderConfig providerClient;

    private MqttClient client;

    private MqttConsumerConfig consumerConfig;

    private ConstantConfig config;

    private final static String upMessage = "message/up";
    private final static String downReply = "message/down";

    public MqttConsumerCallBack(RocketMQHelper rocketMQHelper, ConstantConfig config,MqttProviderConfig providerClient,MqttClient client,MqttConsumerConfig consumerConfig) {
        this.rocketMQHelper = rocketMQHelper;
        this.providerClient = providerClient;
        this.config = config;
        this.client = client;
        this.consumerConfig = consumerConfig;
    }

    /**
     * 客户端断开连接的回调
     */
    @Override
    public void connectionLost(Throwable throwable) {
        throwable.printStackTrace();
        log.info("mqtt消费者={}与服务器断开连接，可重连={}",client.getClientId(),throwable.getMessage());
        while (!client.isConnected()){
            log.info("mqtt消费者重连={}",client.getClientId());
            consumerConfig.connect(client);
        }
    }

    /**
     * 消息到达的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if(StringUtils.isEmpty(topic)){
            return;
        }
        //1.拆分主题，消息内容
        String msg = new String(message.getPayload());
        log.info("mqtt消息接收-messageId={},topic={},msg={}", message.getId(), topic,msg);
        //获取sn
        String[] topicarr = topic.split("/");
        String sn = "";
        if(topicarr!=null && topicarr.length>2){
            sn = topicarr[1];
        }
        String mqTopic="";
        //正常业务上传主题处理
        if(topic.contains(upMessage)){
            Integer lastIndex = topic.lastIndexOf("/");
            mqTopic = topic.substring(lastIndex+1,topic.length());
        }
        if(topic.contains(downReply)){
            Integer lastIndex = topic.lastIndexOf("/");
            // 找到倒数第二个逗号的位置
            int secondLastIndex = topic.lastIndexOf("/", lastIndex - 1);
            mqTopic = topic.substring(secondLastIndex+1,lastIndex);
            mqTopic = mqTopic+"_reply";
        }
        log.info("mqtt消息接收-mqTopic={},sn={}", mqTopic, sn);
        MqMsgDto msgDto = new MqMsgDto();
        msgDto.setSn(sn);
        msgDto.setData(msg);

        //2.rocketmq发送消息到业务服务处理
        rocketMQHelper.asyncSendMsg(mqTopic + "_" + config.getEnv(), JSON.toJSONString(msgDto));
    }

    /**
     * 消息发布成功的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
