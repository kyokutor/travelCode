package cn.itrip.dao;

import cn.itrip.common.Constants;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.util.List;
import java.util.logging.Logger;

public class BaseQuery<T> {

    private HttpSolrClient httpSolrClient;

    private static Logger  logger=Logger.getLogger(String.valueOf(BaseQuery.class));

    //初始化url

    public BaseQuery(String url){
        httpSolrClient=new HttpSolrClient(url);
        httpSolrClient.setParser(new XMLResponseParser());
        httpSolrClient.setConnectionTimeout(500);
    }


    /**
     * 查询分页数据
     */

     public Page<T> quertPage(SolrQuery query,Integer pageNo,Integer pageSize,Class clazz)throws Exception{
         //设置起始页数  pagesize

         Integer rows= EmptyUtils.isEmpty(pageSize)?Constants.DEFAULT_PAGE_SIZE:pageSize;

         Integer currPage=(EmptyUtils.isEmpty(pageNo)?Constants.DEFAULT_PAGE_NO-1:pageNo-1);

            Integer start=currPage*rows;

            query.setRows(rows);
             //一页显示多少条
            query.setStart(start);

         QueryResponse queryResponse = httpSolrClient.query(query);
         SolrDocumentList docs = queryResponse.getResults();
         Page<T> page = new Page(currPage+1, query.getRows(), new Long(docs.getNumFound()).intValue());
         List<T> list = queryResponse.getBeans(clazz);
         page.setRows(list);
         return page;

     }

    /***
     * 使用SolrQuery 查询列表数据
     */
    public List<T> queryList(SolrQuery query, Integer pageSize, Class clazz) throws Exception {
        //设置起始页数
        query.setStart(0);
        //一页显示多少条
        query.setRows(EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize);
        QueryResponse queryResponse = httpSolrClient.query(query);
        List<T> list = queryResponse.getBeans(clazz);
        return list;
    }






}
