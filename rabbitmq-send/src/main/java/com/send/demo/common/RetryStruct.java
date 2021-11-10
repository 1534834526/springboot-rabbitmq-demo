package com.send.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
//重试结构体。用map缓存了每次消息发送记录，在后续的confirm阶段如果成功了就删除，如果失败了就进行定时任务重发
public class RetryStruct {
    private AtomicLong id = new AtomicLong();
    private Map<Long, MessageWithTime> map = new ConcurrentHashMap<>();


    public long generateId() {
        return id.incrementAndGet();
    }

   /* public void add(MessageWithTime msg) {
        map.putIfAbsent(msg.getId(), msg);
    }*/

    public void del(long id) {
        map.remove(id);
    }
}
