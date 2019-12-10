package cn.itrip.service;

import cn.itrip.beans.pojo.ItripUser;

public interface TokenService {

    /*生成toKen  generateToken
    * sace()  保存toKen 返回redis
    *   */
    public String genarateToken (String userAgen, ItripUser user) throws  Exception;


    public void sace(String koken, ItripUser user) throws  Exception;

    public boolean validate(String userAgen,String token) throws  Exception;


    public void  detele(String token)throws Exception;



     public String reloadToken(String userAgent, String token)throws Exception;
}
