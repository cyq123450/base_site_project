package com.cyq.mapper;

import com.cyq.domain.BaseSitePosition;
import com.cyq.domain.CityPosition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 村庄持久层接口
 */
public interface CityPositionMapper {

    /**
     * 删除村庄位置表数据
     */
    void deleteCityPosition();

    /**
     * 保存村庄位置数据
     */
    void saveCityPosition(@Param("datas") List<CityPosition> datas);

    /**
     * 查询基站
     * @param map
     * @return
     */
    List<BaseSitePosition> caculateBaseSitePositions(Map map);

}
