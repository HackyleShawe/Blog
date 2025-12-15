package com.hackyle.blog.admin.module.website.controller;

import com.hackyle.blog.admin.module.website.service.SslCertService;
import com.hackyle.blog.common.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/website/sslCert")
public class SslCertController {
    @Autowired
    private SslCertService sslCertService;

    /**
     * 手动更新证书文件
     * 用户
     *  1.准备证书压缩zip文件
     *  2.指定证书的目标位置
     * 后台：
     *  备份目标位置的同名文件
     *  解压zip文件到目标位置
     *  成功后删除备份文件，失败则恢复原文件
     */
    public ApiResponse<Boolean> handleUpdate() {
        //sslCertService.handleUpdate()
        return ApiResponse.ok();
    }

}
