package com.cyq.controller;

import com.cyq.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/file-upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 基站数据信息上传
     * @param file
     * @return
     */
    @PostMapping("/base-site")
    public String uploadBaseSiteFile(@RequestParam("file") MultipartFile file) {
        try {
            if(file == null){
                return "文件内容为空";
            }else{
                String result = fileUploadService.uploadBaseSiteFile(file);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

}
