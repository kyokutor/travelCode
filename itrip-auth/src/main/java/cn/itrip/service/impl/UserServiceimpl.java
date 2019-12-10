package cn.itrip.service.impl;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;
import cn.itrip.dao.auth.UserLog;
import cn.itrip.dao.user.ItripUserMapper;
import cn.itrip.exception.UserLoginFailedException;
import cn.itrip.service.SmsService;
import cn.itrip.service.UserService;
import cn.itrip.service.mailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("/api")
public class UserServiceimpl implements UserService {

  @Resource
   private ItripUserMapper itripUserMapper;

    @Resource
    private RedisAPI redisAPI;


    @Resource
    private UserLog UserLog;

    @Resource
    private SmsService smsService;

    @Resource
    private mailService mailService;


/*
*   用户名 验证
* */
    public ItripUser login(String userCode, String pwd) throws Exception {
        HashMap<String,Object> Maps=new HashMap<String,Object>();
        Maps.put("userCode",userCode);
        Maps.put("userPassword",MD5.getMd5(pwd,32));
        ItripUser user=UserLog.getItripUserListByMap(Maps);
        if (user!=null&&user.getUserPassword().equals(MD5.getMd5(pwd,32))){
              if(user.getActivated()!=1){
                  throw new UserLoginFailedException("用户未激活");
              }
              return user;
        }

        return null;
    }

    @Override
    /**
     * 发送验证码存入redis  邮箱
     */
    public void itriptxCreateUser(ItripUser user) throws Exception {
        //添加用户信息
              UserLog.insertItripUser(user);
        //生成激活码
           String domd5=MD5.getMd5(new Date().toString(),32);
        //发送邮件，
           mailService.sendActivationMail(user.getUserCode(),domd5) ;
        // 激活码存入redis
           redisAPI.set("activation"+user.getUserCode(),30*60,domd5);  //半个小时

    }

    @Override
    /**
     *  发送
     */
    public void itripxCreateUserByPhone(ItripUser user) throws Exception {
        //创建用户 生成验证码  发送验证码   缓存到redis
        UserLog.insertItripUser(user);

        int code=MD5.getRandomCode();//生成随机数
        //保存用户信息
        smsService.send(user.getUserCode(),"1",new String[]{String.valueOf(code),"1"});

        redisAPI.set("activation"+user.getUserCode(),60,String.valueOf(code));

    }

    //短信验证






    /**  短信验证手机号码
     * @param phoneNumber
     * @param code
     * @return
     */
    public boolean validatePhone(String phoneNumber, String code)throws  Exception{
        String key="activation"+phoneNumber;
        String value=redisAPI.get(key);
        //判断验证码
        if(null!=value&&value.equals(code)){  //值跟redis 的code 等于
          //跟新用户的状态
            ItripUser user=this.finduserBuuserCode(phoneNumber);
            System.out.println("用户的的id"+user.getId());
            if (null!=user){
                  user.setActivated(1);
                  user.setFlatID(user.getId());
                  user.setUserType(0);
              int utor=UserLog.updateItripUser(user);
              if (utor==1){
                  return  true;//跟新成功
              }else{
                  System.out.println("92 行 更新失败  userServierimpl");
                  return false;
              }
            }
        }
           return  false;
    }

    /**
     * 根据用户名查找用户  进行判断
     * @param usercode
     * @return
     * @throws Exception
     */

    @Override
    public ItripUser finduserBuuserCode(String usercode) throws Exception {
        HashMap<String, Object> map=new HashMap<String, Object>();
        map.put("userCode",usercode);
        ItripUser user=UserLog.getItripUserListByMap(map);
        if(null!=user){
            return user;
        }else{
            return null;
        }
    }





    @Override
    public void updateUser(ItripUser user) throws Exception {
        itripUserMapper.updateItripUser(user);
    }

    @Override
    public void deleteUser(Long userId) throws Exception {
        itripUserMapper.deleteItripUserById(userId);
    }

    @Override
    public void changePassword(Long userId, String newPassword) throws Exception {
        ItripUser user =itripUserMapper.getItripUserById(userId);
        user.setUserPassword(newPassword);
        itripUserMapper.updateItripUser(user);
    }

    @Override
    public ItripUser findOne(Long userId) throws Exception {
        return itripUserMapper.getItripUserById(userId);
    }

    @Override
    public List<ItripUser> findAll() throws Exception {
        return itripUserMapper.getItripUserListByMap(null);
    }

    @Override
    public ItripUser findByUsername(String username) throws Exception {
        Map<String, Object> param=new HashMap();
        param.put("userCode", username);
        List<ItripUser> list= itripUserMapper.getItripUserListByMap(param);
        if(list.size()>0)
            return list.get(0);
        else
            return null;
    }

    @Override
    public Set<String> findRoles(String username) {
        return null;
    }

    @Override
    public Set<String> findPermissions(String username) {
        return null;
    }

    @Override
    public boolean activate(String email, String code) throws Exception {
        String key="activation"+email;
        if(redisAPI.exists(key))
            if(redisAPI.get(key).equals(code)){
                ItripUser user=this.findByUsername(email);
                if(EmptyUtils.isNotEmpty(user))
                {
                    System.out.println("激活用户");
                    user.setActivated(1);//激活用户
                    user.setUserType(0);//自注册用户
                    user.setFlatID(user.getId());
                    System.out.println("激活邮箱id"+user.getId());
                    Integer as =itripUserMapper.updateItripUser(user);
                    System.out.println(as+"asasasasas");
                    return true;
                }
            }

        return false;
    }

    @Override
    public void itriptxCreateUserByPhone(ItripUser user) throws Exception {
        //发送短信验证码
        String code=String.valueOf(MD5.getRandomCode());
        smsService.send(user.getUserCode(), "1", new String[]{code,String.valueOf(1)});
        //缓存验证码
        String key="activation:"+user.getUserCode();
        redisAPI.set(key, 1*60, code);
        //保存用户信息
        itripUserMapper.insertItripUser(user);
    }
}

