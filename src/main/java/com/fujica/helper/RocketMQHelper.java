package com.fujica.helper;

import com.fujica.constant.HeaderConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.MDC;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RocketMQHelper {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void  asyncSendMsg(String topic,String msg){
        Message<String> message = MessageBuilder.withPayload(msg)
                // 设置自定义属性，比如 token
                .setHeader(HeaderConstant.TRACE_ID, MDC.get(HeaderConstant.TRACE_ID))
                .build();

        rocketMQTemplate.asyncSend(topic, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                String traceId = message.getHeaders().get(HeaderConstant.TRACE_ID, String.class);
                log.info("traceId = {}, RocketMQ消息发送-成功topic={}",traceId, topic);
            }

            @Override
            public void onException(Throwable e) {
                String traceId = message.getHeaders().get(HeaderConstant.TRACE_ID, String.class);
                log.info("traceId = {}, RocketMQ消息发送-失败topic{}",traceId, topic,e);
            }
        });
    }
}
