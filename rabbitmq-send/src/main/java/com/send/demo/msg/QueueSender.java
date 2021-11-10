package com.send.demo.msg;

public interface QueueSender {

    public boolean sendSCM(String message);

    public boolean sendGkhtOrder(String message);

    public boolean sendGkhtOrder(String message,String key);

    public boolean sendGkhtProduct(String message);
}
