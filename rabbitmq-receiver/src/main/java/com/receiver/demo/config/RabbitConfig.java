package com.receiver.demo.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xjsh
 */
@Configuration
public class RabbitConfig {

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
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
