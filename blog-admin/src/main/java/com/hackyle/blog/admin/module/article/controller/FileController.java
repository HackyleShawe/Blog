package com.hackyle.blog.admin.module.article.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.FileQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.FileUpdateDto;
import com.hackyle.blog.admin.module.article.model.vo.FileVo;
import com.hackyle.blog.admin.module.article.service.FileService;
import com.hackyle.blog.common.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/article/file")
public class FileController {

    @Autowired
    private FileService fileService;


    /**
     * 上传文件
     */
    @PostMapping(value = "/upload")
    public ApiResponse<List<FileVo>> upload(@RequestParam(value = "file", required = false) MultipartFile[] multipartFiles) throws Exception {
        if(multipartFiles == null || multipartFiles.length < 1) {
            return ApiResponse.fail("请选择文件");
        }
        List<String> fileObjects;
        fileObjects = fileService.upload(multipartFiles);

        List<FileVo> fileVoList = new ArrayList<>();
        for (String fileObject : fileObjects) {
            FileVo fileVo = new FileVo();
            fileVo.setFileLink(fileObject);
            fileVo.setCreateTime(LocalDateTime.now());

            fileVoList.add(fileVo);
        }

        return ApiResponse.ok(fileVoList);
    }

    /**
     * TinyMCE富文本编辑器的上传图片专属接口
     * 格式要求：{ location : '/uploaded/image/path/image.png' }
     */
    @PostMapping("/uploadImgTinyMCE")
    public Object uploadImgTinyMCE(@RequestParam(value = "file", required = false) MultipartFile[] multipartFiles) throws Exception{
        if(multipartFiles == null || multipartFiles.length < 1) {
            return ApiResponse.fail("请选择文件");
        }

        List<String> fileObjects = fileService.upload(multipartFiles);

        JSONObject object = new JSONObject();
        object.put("location", fileObjects.get(0));
        return object.toJSONString();

    }

    /**
     * 文件删除
     */
    @DeleteMapping
    public ApiResponse<String> del(@RequestParam("ids") String ids) throws Exception {
        log.info("文件删除-入参ids={}", JSON.toJSONString(ids));
        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }
        if(CollectionUtil.isEmpty(idSet)) {
            throw new IllegalArgumentException("入参id解析错误，请检查");
        }

        boolean result = fileService.del(idSet);
        if(result) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("删除失败");
        }
    }

    /**
     * 文件修改：可以修改文件名、文件链接，移动文件位置
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody FileUpdateDto updateDto) {
        log.info("文件修改-入参updateDto={}", JSON.toJSONString(updateDto));
        if(updateDto == null) {
            throw new IllegalArgumentException("入参不合法");
        }

        boolean status = fileService.update(updateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }


    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(@RequestParam("id") Long id, HttpServletResponse response) throws Exception {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }
        String filePath = fileService.download(id);
        if(StringUtils.isBlank(filePath)) {
            throw new IllegalStateException("文件不存在");
        }

        //String[] filePathSplit = filePath.split(File.pathSeparator);
        //String fileName = filePathSplit[filePathSplit.length - 1];
        File file = new File(filePath);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        //response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/octet-stream;charset=UTF-8");

        //因为是跨前后端分离，默认reponse header只能取到以下：Content-Language，Content-Type，Expires，Last-Modified，Pragma
        //要想获取到文件名，需要采取这种方式。Reference：https://www.cnblogs.com/liuxianbin/p/13035809.html
        String fileName = file.getName();
        response.setHeader("filename", URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setHeader("Access-Control-Expose-Headers","filename");

        response.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bis.readAllBytes());
        outputStream.flush();
        outputStream.close();
        bis.close();
    }

    @PostMapping("/list")
    public ApiResponse<PageInfo<FileVo>> list(@RequestBody FileQueryDto queryDto) throws Exception {
        if(queryDto == null) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        PageInfo<FileVo> fileVoList = fileService.list(queryDto);
        return ApiResponse.ok(fileVoList);
    }

}
