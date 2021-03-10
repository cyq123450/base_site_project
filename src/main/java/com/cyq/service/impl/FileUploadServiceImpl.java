package com.cyq.service.impl;

import com.cyq.domain.BaseSitePosition;
import com.cyq.mapper.BaseSitePositionMapper;
import com.cyq.service.FileUploadService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传服务层实现类
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private BaseSitePositionMapper baseSitePositionMapper;

    @Override
    @Transactional
    public String uploadBaseSiteFile(MultipartFile file) {
        List<BaseSitePosition> datas = processExcelFile(file);
        if (datas == null || datas.size() <= 0) {
            return "Excel为空";
        }
        baseSitePositionMapper.deleteBaseSitePosition();
        baseSitePositionMapper.saveBaseSitePosition(datas);
        return "上传成功";
    }

    /**
     * Excel文件处理
     * @param file
     * @return
     */
    private List<BaseSitePosition> processExcelFile(MultipartFile file) {
        // IO流读取文件
        InputStream input = null;
        XSSFWorkbook wb = null;
        List<BaseSitePosition> datas = new ArrayList<>();
        try {
            input = file.getInputStream();
            wb = new XSSFWorkbook(input);
            // 获取标题
            XSSFSheet sheet = wb.getSheetAt(0);
            for(int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                BaseSitePosition baseSitePosition = new BaseSitePosition();
                // 基站中文名称
                String cnName = row.getCell(0).getStringCellValue();
                baseSitePosition.setCnName(cnName);
                // 基站小区名称
                String lifeName = row.getCell(1).getStringCellValue();
                baseSitePosition.setLifeName(lifeName);
                // 基站经度
                String longitudeTmp = row.getCell(2).getRawValue();
                baseSitePosition.setLongitude(Float.valueOf(longitudeTmp));
                // 基站纬度
                String latitudeTmp = row.getCell(3).getRawValue();
                baseSitePosition.setLatitude(Float.valueOf(latitudeTmp));
                datas.add(baseSitePosition);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datas;
    }

}
