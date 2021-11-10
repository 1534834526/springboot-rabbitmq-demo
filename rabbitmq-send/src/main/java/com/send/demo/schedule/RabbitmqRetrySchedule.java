package com.send.demo.schedule;

import com.alibaba.fastjson.JSON;
import com.send.demo.common.Constants;
import com.send.demo.common.MessageWithTime;
import com.send.demo.msg.QueueSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 定时任务：定时从redis中获取消息重发给MQ
 */
@Slf4j
@Component
public class RabbitmqRetrySchedule {
   /* @Autowired
    private RetryStruct retryStruct;*/

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private QueueSender queueSender;

    @Scheduled(cron = "0 * * * * ?")
    public void retry() {
        long now = System.currentTimeMillis();

        /*for (Map.Entry<Long, MessageWithTime> entry : retryStruct.getMap().entrySet()) {
            MessageWithTime msg = entry.getValue();
            if (null != msg) {
                if (msg.getTime() + Constants.RETRY_TIME_INTERVAL * 3 < now) {
                    log.info("send message {} failed after 3 min ", msg);
                    retryStruct.del(entry.getKey());
                } else if (msg.getTime() + Constants.VALID_TIME < now) {
                    boolean res = msg.getSender().sendGkhtOrder(msg.getMessage());
                    if (!res) {
                        log.info("retry send message failed {}", msg);
                    }
                }
            }else{
                log.info("未发现生产者需要重发的消息！");
            }
        }*/
        Set<String> keys = redisTemplate.keys("RABBIT*");
        for(String key : keys){
            String message  = (String)redisTemplate.opsForValue().get(key);
            if (StringUtils.hasText(message)) {
                MessageWithTime msg = JSON.parseObject(message, MessageWithTime.class);
                if (msg.getTime() + Constants.RETRY_TIME_INTERVAL * 4 < now) {
                    log.info("send message {} failed after 3 min ", msg);
                    redisTemplate.delete(key);
                } else if (msg.getTime() + Constants.VALID_TIME < now) {
                    boolean res = queueSender.sendGkhtOrder(msg.getMessage(),key);
                    if (!res) {
                        log.info("retry send message failed {}", msg);
                    }else{
                        log.info("retry send message success {}", msg);
                    }
                }
            }
        }

    }
}

