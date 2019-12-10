package test;


import cn.itrip.beans.itripHotelVo;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.List;

public class test {
    //请求的url
    public static  String url="http://localhost:8080/solr/hotel";

    public static void main(String[] args) {
       //创建httpSolrClient

        HttpSolrClient httpSolrClient=new HttpSolrClient(url);

        //配置解析器

        httpSolrClient.setParser(new XMLResponseParser());
        httpSolrClient.setConnectionTimeout(500);


        //设置查询参数

        SolrQuery query=new SolrQuery();
        query.setQuery("*:*");
        query.setSort("id",SolrQuery.ORDER.desc);
        query.setStart(0);
        query.setRows(10);

        //接收数据 转化未对象

        QueryResponse queryResponse=null;
        List<itripHotelVo> hotelList=null;
        try {
            queryResponse=httpSolrClient.query(query);
            hotelList=queryResponse.getBeans(itripHotelVo.class);

            for (itripHotelVo holist:hotelList){

                System.out.println(holist.getHotelName());
            }

        }catch (Exception e){
            e.printStackTrace();
        }



    }

}
