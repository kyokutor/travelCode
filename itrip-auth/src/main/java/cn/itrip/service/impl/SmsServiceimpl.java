package cn.itrip.service.impl;

import cn.itrip.service.SmsService;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("smsService")
public class SmsServiceimpl implements SmsService {
    @Override
    public void send(String to, String templateid, String[] datas) throws Exception {


        CCPRestSmsSDK sdk=new CCPRestSmsSDK();
        sdk.init("app.cloopen.com","8883");//初始化生产的url
        sdk.setAccount("8a216da86e011fa3016e88f6ec464ddc","7ce7626f63894b56a4f66b832dc27c9f");
        sdk.setAppId("8a216da86e011fa3016e88f6eca14de3");

        HashMap result=sdk.sendTemplateSMS(to, templateid, datas);

        if ("000000".equals(result.get("statusCode"))){
            System.out.println("发送成功");
        }else {
            System.out.println("发送失败");
            throw new Exception(result.get("statusCode").toString()+":"+result.get("atatusMsg").toString());
        }
    }
}
