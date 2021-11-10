package com.receiver.demo.msg;

import com.rabbitmq.client.Channel;
import com.receiver.demo.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 消息接收者监听queue.scm队列
 *
 * @author xjsh
 */
//queues = RabbitConfig.QUEUE_SCM

// ackMode：none、auto(默认)、manual

//自动确认auto方式
//默认方式：消费者消费此队列消息时，只要监听到第一条消息，所有消息变成未确认状态，而且就会回馈rabbitmq服务，rabbitmq服务会在消费完毕后清空队列消息。
//即当监听第一条消息后，rabbitmq会认为此队列中的所有消息会全部被成功消费，所以当所有消息消费完毕后会移除未确认的所有消息，不管是否全部正常消费，所以可能会导致消息的丢失

//开启手动确认manual的三种方式
//spring.rabbitmq.listener.simple.acknowledge-mode=manual
//factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//ackMode=manual

//1. 手动确认+公平分发消息时，如果未应答，将不再继续向该队列发送消息；
//2. 手动确认+轮询（默认）分发消息时，如果未应答，不影响新消息的消费。
@Component
@RabbitListener(queues = RabbitConfig.QUEUE_GKHT_ORDER,ackMode = "manual")
@Slf4j
public class QueueReceiver {
    @RabbitHandler
    public void process(String message, @Header(AmqpHeaders.DELIVERY_TAG)long deliveryTag, Channel channel,@Headers Map<String,Object> map) throws IOException {
        try {
            //1、队列被监听到后，所有消息变成未确认状态
             log.info("正在消费:{},deliveryTag:{}",message,deliveryTag);
            //2、处理逻辑
            //3、消费逻辑正常处理完毕后，下面这行代码是真正确认应答，删除未确认的消息（未确认的消息其实已经不在队列中了，只是等待确认后删除）
            //第二个参数设置为false，表示删除未确认消息；true，表示需要将这条消息投递给其他的消费者重新消费
            channel.basicAck(deliveryTag, false);
            log.info("消费完成！");
        } catch (Exception e) {
            //4、消费者消费失败，MQ需要将未确认状态的消息重新塞入队列，等待重新消费时，可以使用 basicNack方法，第三个参数true，否认消息，表示这个未确认状态的消息会重新进入队列（变成准备状态）
            log.info("消费失败!");
            channel.basicNack(deliveryTag, false, true);
            //拒绝该消息，消息会被丢弃，不会重回队列,如果配置了死信队列则进入死信队列
            //channel.basicReject(deliveryTag, false);
        }



    };
}
