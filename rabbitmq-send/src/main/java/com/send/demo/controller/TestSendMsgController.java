package com.send.demo.controller;

import com.alibaba.fastjson.JSON;
import com.send.demo.msg.QueueSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/api/v1")
@Api(description = "发送消息管理",  protocols = "http,https")
public class TestSendMsgController {
    @Autowired
    private QueueSender queueSender;

    @ApiOperation(value = "生产者发送消息")
    @PostMapping("/sendMsg")
    public String sendMsg(){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("id",1);
        map.put("name","韩高明");
        map.put("age",18);
        //queueSender.sendSCM(JSON.toJSONString(map));
        queueSender.sendGkhtOrder(JSON.toJSONString(map));
        return "ok";
    }
}
