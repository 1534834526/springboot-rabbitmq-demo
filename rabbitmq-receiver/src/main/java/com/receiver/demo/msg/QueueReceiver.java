package com.receiver.demo.msg;

import com.receiver.demo.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息接收者监听queue.scm队列
 *
 * @author xjsh
 */
@Component
@RabbitListener(queues = RabbitConfig.QUEUE_SCM)
@Slf4j
public class QueueReceiver {
    @RabbitHandler
    public void process(String message) {
        System.out.println("接收消息："+message);
    };
}
