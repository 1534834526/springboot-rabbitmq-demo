package com.send.demo.config;

import com.send.demo.common.RetryStruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author xjsh
 */
@Configuration
@Slf4j
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RetryStruct retryStruct;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        //消息发送给队列过程中失败，是否开启将消息返回给生产者.
        rabbitTemplate.setMandatory(true);
        //不用匿名内部类的方式的处理，配置类需实现ConfirmCallback，ReturnsCallback接口
        /*rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);*/
    }
    public static final String DIRECT_EXCHANGE = "direct.exchange";
    public static final String TOPIC_EXCHANGE = "topic.exchange";
    public static final String FANOUT_EXCHANGE = "fanout.exchange";


    public static final String QUEUE_SCM = "queue.scm";
    public static final String QUEUE_GKHT_ORDER = "queue.gkht.order";
    public static final String QUEUE_GKHT_PRODUCT = "queue.gkht.product";

    //-------- JOSN Message Converter --------

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());

        //YML中需配置走确认回调，这块才执行
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String replyText) {
                if(ack){
                    String id = correlationData.getId();
                    log.info("消息发送到交换器成功！消息ID:{}",id);
                    if(StringUtils.hasText(id)){
                        redisTemplate.delete(id);
                    }
                }else{
                    log.info("消息发送到交换器失败！消息ID:{},失败原因:{}",correlationData.getId(),replyText);
                    //ConfirmCallback中是没有原message消息的，所以无法在这个函数中调用重发，confirm只有一个通知的作用
                }
            }
        });


        //YML中配置走返回回调，这块才执行。
        //版本原因：已经过时
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                String correlationId = new String(message.getMessageProperties().getCorrelationId());
                log.info("消息:{},应答码:{},失败原因:{},交换器:{},路由键:{}",correlationId,replyCode,replyText,exchange,routingKey);
                //进入该方法表示，没路由到具体的队列
                //监听到消息，可以重新投递或者其它方案来提高消息的可靠性。,这里使用了定时任务替换
            }
        });
        //替换为这个（做了封装）
        /*rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                String correlationId = returnedMessage.getMessage().getMessageProperties().getCorrelationId();
                int replyCode = returnedMessage.getReplyCode();
                String replyText = returnedMessage.getReplyText();
                String exchange = returnedMessage.getExchange();
                String routingKey = returnedMessage.getRoutingKey();
                log.info("消息:{},应答码:{},失败原因:{},交换器:{},路由键:{}",correlationId,replyCode,replyText,exchange,routingKey);
                //进入该方法表示，没路由到具体的队列
                //监听到消息，可以重新投递或者其它方案来提高消息的可靠性。,这里使用了定时任务替换
            }
        });*/
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //-------- QUEUE --------------

    @Bean
    public Queue queueScm() {
        return new Queue(QUEUE_SCM,true);
    }
    @Bean
    public Queue queueOrder() {
        return new Queue(QUEUE_GKHT_ORDER,true);
    }

    @Bean
    public Queue queueProduct() {
        return new Queue(QUEUE_GKHT_PRODUCT,true);
    }


    //-------- Exchange --------------
    /**
     * 交换机(Exchange) 描述：接收消息并且将消息转发到绑定的队列，交换机不存储消息。
     * 根据发送消息中的路由键来匹配绑定规则，匹配到，则将消息发送给绑定到此交换器的队列中
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE,true,false);
    }
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE,true,false);
    }
    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE,true,false);
    }

    //-------- Bind（将队列绑定到交换机） --------------
    /**
     * 设置绑定键，将队列绑定到交换机上
     *
     * @return
     */

    @Bean
    Binding bindingExchangeMessage1(Queue queueScm, DirectExchange directExchange) {
        return BindingBuilder.bind(queueScm).to(directExchange).with("queue.scm");
    }

    @Bean
    Binding bindingExchangeMessage2(Queue queueOrder, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueOrder).to(topicExchange).with("#.gkht.order");
    }
    @Bean
    Binding bindingExchangeMessage3(Queue queueProduct, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueProduct).to(topicExchange).with("#.gkht.product");
    }

}
