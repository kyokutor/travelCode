package cn.itrip.service;

import cn.itrip.beans.itripHotelVo;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.Page;

import java.util.List;

public interface SearchHotelService {

    /**
     * 搜索旅馆
     * @param vo
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Page<itripHotelVo> searchltripHotelPage(SearchHotelVO vo, Integer pageNo, Integer pageSize) throws Exception;

    /**
     *  根据热门城市查询酒店
     * @param cityld
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<itripHotelVo> searchltripHotelistByHootCity(Integer cityld, Integer pageSize) throws Exception;


}
