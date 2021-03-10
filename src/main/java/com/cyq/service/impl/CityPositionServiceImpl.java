package com.cyq.service.impl;

import com.cyq.domain.BaseSitePosition;
import com.cyq.domain.CityPosition;
import com.cyq.mapper.CityPositionMapper;
import com.cyq.service.CityPositionService;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 村庄位置服务层接口实现类
 */
@Service
public class CityPositionServiceImpl implements CityPositionService {

    @Value("${dezhou.url}")
    private String DEZHOU_CODE_URL;

    @Value("${dezhou.code}")
    private String DEZHOU_CITY_ID;

    private static final double EARTH_RADIUS = 6378.137;

    @Autowired
    private CityPositionMapper cityPositionMapper;

    private static Map<Integer, String> cssMap = new HashMap<>();

    static {
        cssMap.put(3, "countytr");// 县
        cssMap.put(4, "towntr");// 镇
        cssMap.put(5, "villagetr");// 村
    }

    @Override
    @Transactional
    public String syncCityInfo() throws IOException {
        List<CityPosition> cityPositions = new ArrayList<>();
        parseCityCode(cityPositions, DEZHOU_CITY_ID);
        if (cityPositions.size() <= 0) {
            return "同步成功";
        }
        cityPositionMapper.deleteCityPosition();
        cityPositionMapper.saveCityPosition(cityPositions);
        return "同步成功";
    }

    @Override
    public List<BaseSitePosition> calculationBaseSitePosition(CityPosition cityPosition) {
        float cityLongitude = cityPosition.getLongitude();  // 村庄精度
        float cityLatitude = cityPosition.getLatitude();    // 村庄维度
        Double r = cityPosition.getR();                     // 村庄半径
        Map<String, Double> longitudeMap = caculateLongitude(cityLongitude, r);
        Map<String, Double> latitudeMap = caculateLatitude(cityLatitude, r);
        Map param = new HashMap<>();
        param.put("long1", String.format("%.6f", longitudeMap.get("l1")));
        param.put("long2", String.format("%.6f", longitudeMap.get("l2")));
        param.put("lat1", String.format("%.6f", latitudeMap.get("l1")));
        param.put("lat2", String.format("%.6f", latitudeMap.get("l2")));
        List<BaseSitePosition> baseSitePositions = cityPositionMapper.caculateBaseSitePositions(param);
        if (baseSitePositions != null && baseSitePositions.size() > 0) {
            for(BaseSitePosition baseSitePosition : baseSitePositions) {
                double distance = getDistance(baseSitePosition.getLongitude(),
                        baseSitePosition.getLatitude(),
                        cityPosition.getLongitude(),
                        cityPosition.getLatitude());
                baseSitePosition.setDistance(Double.valueOf(String.format("%.2f", Double.valueOf(distance))));
            }
        }

        return baseSitePositions;
    }

    /**
     * 精度差值计算
     * @param sourceLongitude
     * @param r
     * @return
     */
    private Map<String, Double> caculateLongitude(double sourceLongitude, double r) {
        double a = r / 111000;
        Map<String, Double> result = new HashMap();
        double l1 = sourceLongitude - a;
        double l2 = sourceLongitude + a;
        result.put("l1", l1);
        result.put("l2", l2);
        return result;
    }

    /**
     * 维度差值计算
     * @param sourceLatitude
     * @param r
     * @return
     */
    private Map<String, Double> caculateLatitude(double sourceLatitude, double r) {
        double a = r /(111000 * Math.cos(sourceLatitude));
        Map<String, Double> result = new HashMap();
        double l1 = sourceLatitude - a;
        double l2 = sourceLatitude + a;
        result.put("l1", l1);
        result.put("l2", l2);
        return result;
    }

    // 经纬度距离计算
    private double getDistance(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo) {
        GlobalCoordinates source = new GlobalCoordinates(latitudeFrom, longitudeFrom);
        GlobalCoordinates target = new GlobalCoordinates(latitudeTo, longitudeTo);

        return new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere, source, target).getEllipsoidalDistance();
    }

    private void parseCityCode(List<CityPosition> cityPositions, String parentId) throws IOException {
        int level = 3;
        Document connect = connect(DEZHOU_CODE_URL);
        if (connect != null) {
            Elements elements = connect.select("tr." + cssMap.get(level));
            for(Element element : elements) {
                Elements elements1 = element.select("td");
                List<String> names = elements1.eachText();
                String cityCode = names.get(0);
                String cityName = names.get(1);

                try {
                    Long.valueOf(cityCode);
                } catch (Exception e) {
                    String tmp = cityCode;
                    cityCode = cityName;
                    cityName = tmp;
                }

                CityPosition cityPosition = new CityPosition();
                cityPosition.setId(cityCode);
                cityPosition.setName(cityName);
                cityPosition.setParentId(parentId);
                cityPositions.add(cityPosition);

                Elements select = element.select("a");
                for(Element element1 : select) {
                    parseNextLevel(element1, level + 1, cityPositions, cityCode);
                }



            }
        }
    }

    private void parseNextLevel(Element parentElement, int level, List<CityPosition> datas, String parentId) throws IOException
    {
        /*try
        {
            Thread.sleep(500);//睡眠一下，否则可能出现各种错误状态码
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/

        Document doc = connect(parentElement.attr("abs:href"));
        if (doc != null)
        {
            Elements newsHeadlines = doc.select("tr." + cssMap.get(level));//
            // 获取表格的一行数据
            for (Element element : newsHeadlines)
            {
                Elements elements1 = element.select("td");
                List<String> names = elements1.eachText();
                String cityCode = names.get(0);
                String cityName = names.get(1);

                try {
                    Long.valueOf(cityCode);
                } catch (Exception e) {
                    String tmp = cityCode;
                    cityCode = cityName;
                    cityName = tmp;
                }

                CityPosition cityPosition = new CityPosition();
                cityPosition.setId(cityCode);
                cityPosition.setName(cityName);
                cityPosition.setParentId(parentId);
                datas.add(cityPosition);

                Elements select = element.select("a");// 在递归调用的时候，这里是判断是否是村一级的数据，村一级的数据没有a标签
                if (select.size() != 0)
                {
                    parseNextLevel(select.last(), level + 1, datas, cityCode);
                }
            }
        }
    }

    /**
     * 通过URL获取文档
     * @param url
     * @return
     */
    private Document connect(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(1000 * 10).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

}
