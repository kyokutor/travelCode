package cn.itrip.service;

import cn.itrip.beans.itripHotelVo;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.common.PropertiesUtils;
import cn.itrip.dao.BaseQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("searchHotelService")
public class SearchHotelServiceimpl implements  SearchHotelService {

    public static String URL = PropertiesUtils.get("database.properties", "baseUrl");

    private BaseQuery<itripHotelVo> itripHotelVOBaseQuery = new BaseQuery(URL);

    /**
     * 根据多个条件查询酒店分页
     * @param vo
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Page<itripHotelVo> searchltripHotelPage(SearchHotelVO vo, Integer pageNo, Integer pageSize) throws Exception {
        SolrQuery query = new SolrQuery("*:*");
        StringBuffer tempQuery = new StringBuffer();
        int tempFlag = 0;
        if (EmptyUtils.isNotEmpty(vo)) {
            if (EmptyUtils.isNotEmpty(vo.getDestination())) {
                tempQuery.append(" destination :" + vo.getDestination());
                tempFlag = 1;
            }
            if (EmptyUtils.isNotEmpty(vo.getHotelLevel())) {
                query.addFilterQuery("hotelLevel:" + vo.getHotelLevel() + "");
            }
            if (EmptyUtils.isNotEmpty(vo.getKeywords())) {
                if (tempFlag == 1) {
                    tempQuery.append(" AND keyword :" + vo.getKeywords());
                } else {
                    tempQuery.append(" keyword :" + vo.getKeywords());
                }
            }
            if (EmptyUtils.isNotEmpty(vo.getFeatureIds())) {
                StringBuffer buffer = new StringBuffer("(");
                int flag = 0;
                String featureIdArray[] = vo.getFeatureIds().split(",");
                for (String featureId : featureIdArray) {
                    if (flag == 0) {
                        buffer.append(" featureIds:" + "*," + featureId + ",*");
                    } else {
                        buffer.append(" OR featureIds:" + "*," + featureId + ",*");
                    }
                    flag++;
                }
                buffer.append(")");
                query.addFilterQuery(buffer.toString());
            }
            if (EmptyUtils.isNotEmpty(vo.getTradeAreaIds())) {
                StringBuffer buffer = new StringBuffer("(");
                int flag = 0;
                String tradeAreaIdArray[] = vo.getTradeAreaIds().split(",");
                for (String tradeAreaId : tradeAreaIdArray) {
                    if (flag == 0) {
                        buffer.append(" tradingAreaIds:" + "*," + tradeAreaId + ",*");
                    } else {
                        buffer.append(" OR tradingAreaIds:" + "*," + tradeAreaId + ",*");
                    }
                    flag++;
                }
                buffer.append(")");
                query.addFilterQuery(buffer.toString());
            }
            if (EmptyUtils.isNotEmpty(vo.getMaxPrice())) {
                query.addFilterQuery("minPrice:" + "[* TO " + vo.getMaxPrice() + "]");
            }
            if (EmptyUtils.isNotEmpty(vo.getMinPrice())) {
                query.addFilterQuery("minPrice:" + "[" + vo.getMinPrice() + " TO *]");
            }

            if (EmptyUtils.isNotEmpty(vo.getAscSort())) {
                query.addSort(vo.getAscSort(), SolrQuery.ORDER.asc);
            }

            if (EmptyUtils.isNotEmpty(vo.getDescSort())) {
                query.addSort(vo.getDescSort(), SolrQuery.ORDER.desc);
            }
        }
        if (EmptyUtils.isNotEmpty(tempQuery.toString())) {
            query.setQuery(tempQuery.toString());
        }
        Page<itripHotelVo> page = itripHotelVOBaseQuery.quertPage(query, pageNo, pageSize, itripHotelVo.class);
        return page;
    }



    /**
     * 根据热门城市查询酒店
     * @param cityld
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<itripHotelVo> searchltripHotelistByHootCity(Integer cityld, Integer pageSize) throws Exception {
        SolrQuery query = new SolrQuery("*:*");
        if (EmptyUtils.isNotEmpty(cityld)) {
            query.addFilterQuery("cityId:" + cityld);
        } else {
            return null;
        }
        List<itripHotelVo> hotelVOList = itripHotelVOBaseQuery.queryList(query, pageSize,itripHotelVo.class);
        return hotelVOList;
    }
}
