package com.cyq.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基站实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseSitePosition {

    // 基站经度
    private float longitude;
    // 基站纬度
    private float latitude;
    // 基站中文名称
    private String cnName;
    // 基站小区名称
    private String lifeName;
    // 距目标点距离
    private Double distance;

}
