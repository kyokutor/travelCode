package cn.itrip.dao.auth;

import cn.itrip.beans.pojo.ItripUser;

import java.util.HashMap;

public interface UserLog {

    /* 判断用户*/


    public ItripUser getItripUserListByMap(HashMap<String,Object> map) throws  Exception;

    public Integer insertItripUser(ItripUser itripUser)throws Exception;

    public Integer updateItripUser(ItripUser itripUser)throws Exception;
}
