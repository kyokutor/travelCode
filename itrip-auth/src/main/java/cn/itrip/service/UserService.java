package cn.itrip.service;

import cn.itrip.beans.pojo.ItripUser;

import java.util.List;
import java.util.Set;

public interface UserService {

    /**
     *  判断用户名密码
      */
     public ItripUser login(String userCode,String pwd) throws  Exception;


     public void  itriptxCreateUser(ItripUser user) throws Exception;

   //发送验证码
     public void itripxCreateUserByPhone(ItripUser user) throws Exception;

     //判断验证码
    public boolean validatePhone(String phoneNumber, String code)throws  Exception;


    //查询用户信息

      public ItripUser finduserBuuserCode(String usercode)throws  Exception;








    public void updateUser(ItripUser user) throws Exception;

    public void deleteUser(Long userId) throws Exception;

    public void changePassword(Long userId, String newPassword) throws Exception;

    ItripUser findOne(Long userId) throws Exception;

    List<ItripUser> findAll() throws Exception;

    public ItripUser findByUsername(String username) throws Exception;

    public Set<String> findRoles(String username);

    public Set<String> findPermissions(String username);


    /**
     * 邮箱激活
     * @param email 用户注册油箱
     * @param code 激活码
     * @return
     * @throws Exception
     */
    public boolean activate(String email,String code) throws Exception;

    /**
     * 使用手机号创建用户账号
     * @param user
     * @throws Exception
     */
    public void itriptxCreateUserByPhone(ItripUser user) throws Exception;



}
