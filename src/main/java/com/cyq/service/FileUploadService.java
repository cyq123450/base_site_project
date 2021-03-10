package com.cyq.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务层接口
 */
public interface FileUploadService {

    /**
     * 基站数据信息上传
     * @param file
     * @return
     */
    String uploadBaseSiteFile(MultipartFile file);

}
