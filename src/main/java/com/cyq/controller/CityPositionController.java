package com.cyq.controller;

import com.cyq.domain.BaseSitePosition;
import com.cyq.domain.CityPosition;
import com.cyq.service.CityPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 村庄位置控制器
 */
@Controller
@RequestMapping("/api/city")
public class CityPositionController {

    @Autowired
    private CityPositionService cityPositionService;

    /**
     * 同步村庄信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/sync-city-info")
    public String syncCityInfo() {
        try {
            cityPositionService.syncCityInfo();
            return "同步成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "同步失败";
        }
    }

    @GetMapping("/caculate")
    public ModelAndView calculationBaseSitePosition(@RequestParam("city.longitude") String longitude,
                                              @RequestParam("city.latitude") String latitude,
                                              @RequestParam("city.r") String r) {
        CityPosition cityPosition = new CityPosition();
        ModelAndView model = new ModelAndView("index");
        try {
            cityPosition.setLatitude(Double.valueOf(latitude).floatValue());
            cityPosition.setLongitude(Double.valueOf(longitude).floatValue());
            cityPosition.setR(Double.valueOf(r).doubleValue());
            List<BaseSitePosition> baseSitePositions = cityPositionService.calculationBaseSitePosition(cityPosition);
            model.addObject("city", cityPosition);
            model.addObject("sites", baseSitePositions);
            return model;
        } catch (Exception e) {
            e.printStackTrace();
            model.addObject("city", cityPosition);
            return model;
        }
    }

}
