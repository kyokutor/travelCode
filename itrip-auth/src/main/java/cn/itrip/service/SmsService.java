package cn.itrip.service;

public interface SmsService {

    public void send(String to,String templateid,String[] datas) throws Exception;//发给那个  模板id  发送内容
}
