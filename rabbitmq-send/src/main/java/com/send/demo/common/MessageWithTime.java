package com.send.demo.common;

import com.send.demo.msg.QueueSender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created by littlersmall on 2018/5/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
public class MessageWithTime {
    private String id;
    private long time;
    private String message;
    private QueueSender sender;
}
