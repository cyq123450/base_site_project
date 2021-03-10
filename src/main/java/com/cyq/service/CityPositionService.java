package com.cyq.service;

import com.cyq.domain.BaseSitePosition;
import com.cyq.domain.CityPosition;

import java.io.IOException;
import java.util.List;

/**
 * 村庄位置服务层接口
 */
public interface CityPositionService {

    /**
     * 同步村庄信息
     * @return
     */
    String syncCityInfo() throws IOException;

    /**
     * 计算基站位置
     * @param cityPosition
     * @return
     */
    List<BaseSitePosition> calculationBaseSitePosition(CityPosition cityPosition);

}
