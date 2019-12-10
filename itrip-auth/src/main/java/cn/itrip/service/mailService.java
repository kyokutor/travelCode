package cn.itrip.service;

public interface mailService {


    //发送激活码    发送给谁  激活码内容
    public  void  sendActivationMail(String mailTO,String  activationCode) throws Exception;
}
