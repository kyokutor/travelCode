package cn.itrip.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisAPI {
    /*redis 192.168.1.80
    *   get  查看
    *    set  写入
    *  exists  判断
    * ttl  有效期
    * del  删除
    * */
    public JedisPool jedisPool;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }






/*
*   get
* */
    public String get(String key){
        Jedis jedisPoooa=jedisPool.getResource();
       String value=jedisPoooa.get(key);
        jedisPool.returnResource(jedisPoooa);
        return value;
    }
/*  set*/


    public String set(String key,String value){
        Jedis jedis=jedisPool.getResource();
        String uis=jedis.set(key,value);
        jedisPool.returnResource(jedis);
     return uis;
    }

    public String set(String key,int ex,String value){
        Jedis jedis=jedisPool.getResource();
        String uis=jedis.setex(key,ex,value);
        jedisPool.returnResource(jedis);
        return uis;
    }


/*
*   Jedis jedis=jedisPool.getResource();
*    判断
* */
    public boolean exists(String key){
        Jedis jedis=jedisPool.getResource();
        boolean lan=jedis.exists(key);
        jedisPool.returnResource(jedis);
        return lan;
    }



    /*
    *   有效期
    * */

    public long ttl(String key){
        Jedis jedis=jedisPool.getResource();
        long tt=jedis.ttl(key);
        jedisPool.returnResource(jedis);
        return tt;
    }


/*  删除*/

    public long det(String key){
        Jedis jedis=jedisPool.getResource();
        long tt=jedis.del(key);
        jedisPool.returnResource(jedis);
        return tt;
    }



}
