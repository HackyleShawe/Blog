package com.hackyle.blog.admin.module.article.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.infrastructure.config.ResConfig;
import com.hackyle.blog.admin.module.article.mapper.FileMapper;
import com.hackyle.blog.admin.module.article.model.dto.FileQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.FileUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.FileEntity;
import com.hackyle.blog.admin.module.article.model.vo.FileVo;
import com.hackyle.blog.admin.module.article.service.FileService;
import com.hackyle.blog.common.enums.ArticleFileTypeEnum;
import com.hackyle.blog.common.enums.ImageTypeEnum;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.util.PageHelperUtils;
import com.hackyle.blog.common.util.WaterMarkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {
    @Autowired
    private FileMapper fileMapper;

    /**
     * res开头的配置项
     */
    @Autowired
    private ResConfig resConfig;

    @Override
    public List<String> upload(MultipartFile[] multipartFiles) throws Exception {
        List<FileEntity> fileEntities = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            InputStream inputStream = file.getInputStream(); //得到文件流
            String fileName = file.getOriginalFilename(); //文件名
            //String contentType = file.getContentType();  //类型
            long sourceSize = file.getSize();

            String pathSplit = pathSplitByDate();
            String nameByUUID = nameByUUID(fileName);
            File storagePathFile = storagePath(pathSplit);

            String targetFile = storagePathFile.getAbsolutePath() + File.separator + nameByUUID;
            FileOutputStream outputStream = new FileOutputStream(targetFile);

            FileEntity fileEntity = new FileEntity();
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            //如果是图片，则需要加水印，然后再写出到指定位置
            if (ImageTypeEnum.ofCode(fileType) != null) {
                WaterMarkUtils.markByText(resConfig.getWaterMarkText(), inputStream, outputStream, fileType);
                fileEntity.setFileType(ArticleFileTypeEnum.IMAGE.getCode());
            } else {
                long transferSize = inputStream.transferTo(outputStream);
                if (sourceSize != transferSize) {
                    throw new RuntimeException("File Storage Fail!");
                }
                fileEntity.setFileType(ArticleFileTypeEnum.BIN.getCode());
            }

            //关闭流，否则删除访问文件时显示被占用，导致删除失败
            inputStream.close();
            outputStream.close();

            //设置文件为可读
            Files.setPosixFilePermissions(Paths.get(targetFile), PosixFilePermissions.fromString("rw-r--r--"));

            fileEntity.setFileLink(resConfig.getDomain() + pathSplit + nameByUUID);
            fileEntities.add(fileEntity);
        }

        //每次上传图片，就落库
        boolean saved = this.saveBatch(fileEntities);
        if (!saved) {
            throw new BizException("图片上传保存失败");
        }
        log.info("图片上传保存-fileObjects={},inserted={}", JSON.toJSONString(fileEntities), saved);

        return fileEntities.stream().map(FileEntity::getFileLink).collect(Collectors.toList());
    }

    /**
     * 根据当时日期生成文件前缀的路径：/年/月/
     */
    private String pathSplitByDate() {
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();
        return String.format("%s/%s/", localDate.getYear(), month < 10 ? "0" + month : String.valueOf(month));
    }

    /**
     * 以UUID命名文件
     *
     * @param originalFileName 原始名字
     * @return 时间戳转换后的新名字
     */
    private String nameByUUID(String originalFileName) {
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名为空");
        }
        return UUID.randomUUID().toString().replaceAll("-", "") + originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    /**
     * 拼接完整的存储路径
     */
    private File storagePath(String pathSplit) {
        if (StringUtils.isBlank(resConfig.getStoragePath())) {
            throw new RuntimeException("The file-storage-path can't be null!");
        }

        File file = new File(resConfig.getStoragePath() + pathSplit);

        if (!file.exists()) {
            if (file.mkdirs()) {
                log.info("The file-storage-path haven't exists, it have been created!");
            }
        }

        if (!file.isDirectory()) {
            throw new RuntimeException("The file-storage-path is not a directory!");
        }

        return file;
    }

    /**
     * 保存文章中的图片，并关联到文章中
     * @param articleId 文章ID
     * @param imgUrls 从文章内容中解析出的图片连接
     */
    @Override
    public boolean saveImgFile(long articleId, Set<String> imgUrls) {
        //找出已经上传但是没有归属到具体文章的图片。注意：如果本次修改没有新增图片，则将会没有数据
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileEntity::getArticleId, 0);
        queryWrapper.eq(FileEntity::getDeleted, Boolean.FALSE);
        List<FileEntity> newImgFiles = this.list(queryWrapper);
        //如果有新增图片，则将其关联到该文章中
        if(CollectionUtil.isNotEmpty(newImgFiles)) {
            //从已经上传但是还没有归属到具体文章的图片中，过滤出本次文章保存的图片
            List<FileEntity> existsImgFiles = newImgFiles.stream()
                    .filter(ele -> imgUrls.contains(ele.getFileLink()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(existsImgFiles)) {
                List<Long> existsIds = existsImgFiles.stream().map(FileEntity::getId).distinct().collect(Collectors.toList());
                LambdaUpdateWrapper<FileEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(FileEntity::getId, existsIds);
                updateWrapper.set(FileEntity::getArticleId, articleId);
                boolean update = this.update(updateWrapper);
                if (!update) {
                    throw new BizException("关联已上传的图片与文章失败");
                }
            }
        }

        //找出文章中已经删除的图片链接，并删除
        List<FileEntity> articleImgFiles = this.list(Wrappers.<FileEntity>lambdaQuery().eq(FileEntity::getArticleId, articleId));
        if (CollectionUtil.isNotEmpty(articleImgFiles)) {
            List<FileEntity> delImgFiles = articleImgFiles.stream()
                    .filter(ele -> !imgUrls.contains(ele.getFileLink()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(delImgFiles)) {
                List<Long> delIds = delImgFiles.stream().map(FileEntity::getId).distinct().collect(Collectors.toList());
                boolean removed = this.removeBatchByIds(delIds);
                if (!removed) {
                    throw new BizException("删除文章内容已经删除的图片失败");
                }
            }

        }

        return true;
    }

    @Override
    public boolean del(Set<Long> ids) {
        List<FileEntity> fileEntities = this.listByIds(ids);
        if (CollectionUtil.isNotEmpty(fileEntities)) {
            return false;
        }

        List<Long> delIds = new ArrayList<>();
        for (FileEntity fileEntity : fileEntities) {
            delIds.add(fileEntity.getId());
            String filePath = fileEntity.getFileLink().substring(resConfig.getDomain().length());
            String fullPath = resConfig.getStoragePath() + filePath;
            File file = new File(fullPath);
            file.delete(); //注意：如果文件删除失败，数据库执行成功，则这个文件将不会被清除
        }

        //返回值的含义是：操作是否执行成功，而不是是否真的删除了数据
        boolean del = this.removeBatchByIds(delIds);
        log.info("文件删除-delIds={},deleted={}", JSON.toJSONString(delIds), del);

        return del;
    }

    @Override
    public boolean update(FileUpdateDto updateDto) {
        Long id = updateDto.getId();
        FileEntity fileEntity = this.getById(id);
        if (fileEntity == null) {
            throw new BizException("文件不存在");
        }

        //TODO

        return true;
    }

    @Override
    public String download(Long id) {
        FileEntity fileEntity = this.getById(id);
        if (fileEntity == null) {
            throw new BizException("文件链接不存在");
        }

        String filePath = fileEntity.getFileLink().substring(resConfig.getDomain().length());
        String fullPath = resConfig.getStoragePath() + filePath;
        File file = new File(fullPath);
        if(!file.exists()) {
            throw new BizException("文件不存在");
        }
        return fullPath;
    }

    @Override
    public PageInfo<FileVo> list(FileQueryDto queryDto) {
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileEntity::getDeleted, Boolean.FALSE)
                .eq(queryDto.getArticleId() != null, FileEntity::getArticleId, queryDto.getArticleId())
                .eq(queryDto.getFileLink() != null, FileEntity::getFileLink, queryDto.getFileLink())
                .eq(queryDto.getEmptyArticle() != null && queryDto.getEmptyArticle(), FileEntity::getArticleId, 0);

        PageHelper.startPage(queryDto.pageNum, queryDto.pageSize);
        List<FileEntity> fileEntityList = this.list(queryWrapper);

        return PageHelperUtils.getPageInfo(fileEntityList, FileVo.class);
    }

}
