package com.send.demo.msg;

import com.alibaba.fastjson.JSON;
import com.send.demo.common.MessageWithTime;
import com.send.demo.common.RetryStruct;
import com.send.demo.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 消息发送者发送消息到队列
 *
 * @author
 */
@Slf4j
@Service
public class QueueSenderImpl implements QueueSender {
    private static final String RABBIT = "RABBIT:";
    private static final String RABBIT_SEND = "RABBIT_SEND:";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RetryStruct retryStruct;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * direct
     * @param message
     */
    @Override
    public boolean sendSCM(String message) {
        //RabbitConfig.QUEUE_SCM=="queue.scm"
        try{
            String key = RABBIT+RABBIT_SEND + UUID.randomUUID().toString();
            CorrelationData data = getCorrelationData(message,key);
            this.rabbitTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE,"queue.scm", message,data);
        } catch (Exception e) {
            log.error("send failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * topic
     * @param message
     */
    @Override
    public boolean sendGkhtOrder(String message) {
        try{
            String key = RABBIT+RABBIT_SEND + UUID.randomUUID().toString();
            CorrelationData data = getCorrelationData(message,key);
            this.rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE,"queue.gkht.order", message,data);
        } catch (Exception e) {
            log.error("send failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean sendGkhtOrder(String message,String key) {
        try{
            CorrelationData data = getCorrelationData(message,key);
            this.rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE,"queue.gkht.order", message,data);
        } catch (Exception e) {
            log.error("send failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * topic
     * @param message
     */
    @Override
    public boolean sendGkhtProduct(String message) {
        String key = RABBIT+RABBIT_SEND + UUID.randomUUID().toString();
        CorrelationData data = getCorrelationData(message,key);
        try {
            this.rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE,"queue.gkht.product", message,data);
        } catch (Exception e) {
            log.error("send failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    protected CorrelationData getCorrelationData(String message,String key) {
        //本地缓存（后面可以替换为redis）
        /*long id = retryStruct.generateId();
        MessageWithTime messageWithTime = new MessageWithTime(id, time, message,this);
        retryStruct.add(messageWithTime);
        return new CorrelationData(String.valueOf(message.getId()));*/
        //redis
        long time = System.currentTimeMillis();
        MessageWithTime messageWithTime = new MessageWithTime(key, time, message,this);
        redisTemplate.opsForValue().setIfAbsent(key, JSON.toJSONString(messageWithTime));
        return new CorrelationData(key);
    }
}
