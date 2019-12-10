package cn.itrip.service.impl;

import cn.itrip.service.mailService;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("mailService")
public class mailServiceimpl implements mailService {

     @Resource
    private SimpleMailMessage mailMessage;


     @Resource
     private MailSender mailSender;

    @Override
    public void sendActivationMail(String mailTO, String activationCode) throws Exception {

        mailMessage.setTo(mailTO);
        mailMessage.setText("【i旅行】 您的激活码是："+activationCode);
         mailSender.send(mailMessage);
    }
}
