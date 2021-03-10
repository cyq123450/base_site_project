package com.cyq.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 村庄经纬度
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityPosition {
    // 村庄ID
    private String id;
    // 村庄名称
    private String name;
    // 父ID
    private String parentId;
    // 经度
    private float longitude;
    // 纬度
    private float latitude;
    // 半径距离
    private Double r;
}
