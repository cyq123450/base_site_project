package com.cyq.mapper;

import com.cyq.domain.BaseSitePosition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基站持久层接口
 */
public interface BaseSitePositionMapper {

    /**
     * 删除基站位置表数据
     */
    void deleteBaseSitePosition();

    /**
     * 保存基站位置数据
     */
    void saveBaseSitePosition(@Param("datas") List<BaseSitePosition> datas);

}
