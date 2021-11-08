package com.send.demo.msg;

import com.send.demo.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息发送者发送消息到队列
 *
 * @author xjsh
 */
@Component
public class QueueSender {

    private final AmqpTemplate rabbitTemplate;

    @Autowired
    public QueueSender(final AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * direct
     * @param message
     */
    public void sendSCM(String message) {
        //RabbitConfig.QUEUE_SCM=="queue.scm"
        this.rabbitTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE,"queue.scm", message);
    }

    /**
     * topic
     * @param message
     */
    public void sendGkhtOrder(String message) {
        this.rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE,"queue.gkht.order", message);
    }

    /**
     * topic
     * @param message
     */
    public void sendGkhtProduct(String message) {
        this.rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE,"queue.gkht.product", message);
    }
}
