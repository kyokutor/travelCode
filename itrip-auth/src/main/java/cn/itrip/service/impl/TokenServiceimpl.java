package cn.itrip.service.impl;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;
import cn.itrip.service.TokenService;
import com.alibaba.fastjson.JSON;
import nl.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service("TokenService")
public class TokenServiceimpl implements TokenService {

    @Resource
    private RedisAPI redisAPI;


    /**
     *   生成token 参数
     * @param userAgen
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public String genarateToken(String userAgen, ItripUser user) throws Exception {
         StringBuilder str=new StringBuilder();
          str.append("token:");
        UserAgent agent= UserAgent.parseUserAgentString(userAgen);
          /*获取操作系统*/
        if(agent.getOperatingSystem().isMobileDevice()){
                str.append("MOBILE");//表示移动设备
        }else{
                str.append("PC-");
        }
        str.append(MD5.getMd5(user.getUserCode(),32)+"-");
        str.append(user.getId()+"-");
        str.append(new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date())+"-");
        str.append(MD5.getMd5(userAgen,6));
        return str.toString();
    }

    /**
     * 保存用户信息
     * @param token
     * @param user
     * @throws Exception
     */
    @Override
    public void sace(String token, ItripUser user) throws Exception {
             if(token.startsWith("token:PC-")){
                 /*pc端口*/
                   redisAPI.set(token,2*60*60, JSON.toJSONString(user));
             }else {
                 //移动端口
                   redisAPI.set(token,JSON.toJSONString(user));
             }
    }

    /**
     * 验证token
     * @param userAgen
     * @param token
     * @return
     * @throws Exception
     */
    public boolean validate(String userAgen, String token) throws Exception {
        System.out.println(userAgen+"==="+token);
        if (!redisAPI.exists(token)) {
            return false;  //验证失败
        }
        //按"-"进行分割后取第四位，即得到使用MD5对http请求中的user-agent加密，生成的六位随机数。
        //token:PC-ed6e201becad0e79ae04178e519fd13b-29-201911201902054-2894fb
        // token:客户端标识-USERCODE-USERID-CREATIONDATE-RANDEM[6位]
        String agentMD5 = token.split("-")[4];
        System.out.println("token"+MD5.getMd5(userAgen,6)+"=="+agentMD5);
        if (!MD5.getMd5(userAgen, 6).equals(agentMD5)) {
            return false;
        }
        //不用去验证token是否在有效期中，首先如果是PC端的用户，token有效期为2h，在2h后在redis中会被自动删除。
        //然后在移动端的用户信息是永久有效到redis缓存数据库中。
        return true;
    }

    @Override
    public void detele(String token) throws Exception {
        redisAPI.det(token);
    }



    private long redteate=30*60;//1800秒 30分钟    生成时间有没有超过置换保护时间
    private int  logdate=2*60;  //延迟过期
    @Override
    public String reloadToken(String userAgent, String token) throws Exception {
        //验证token是否有效
        if(!redisAPI.exists(token)){
            throw new Exception("token无效");
        }
        Date gettime=new SimpleDateFormat("yyyyMMddhhmmsss").parse(token.split("-")[3]);
        long dat= Calendar.getInstance().getTimeInMillis()-gettime.getTime();
        //判断保护期时间
        if(dat<redteate*1000){
            throw new Exception("token保护时间");
        }
        ItripUser iruser=JSON.parseObject(redisAPI.get(token),ItripUser.class);
        //转换
        String newTken=this.genarateToken(userAgent,iruser);

        //延迟
           redisAPI.set(token,logdate,redisAPI.get(token));
         //保存新的

         this.sace(token,iruser);
          return newTken;
    }

}
