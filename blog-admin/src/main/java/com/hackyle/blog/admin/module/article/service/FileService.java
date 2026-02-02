package com.hackyle.blog.admin.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.FileQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.FileUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.FileEntity;
import com.hackyle.blog.admin.module.article.model.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface FileService extends IService<FileEntity> {
    List<String> upload(MultipartFile[] multipartFiles) throws Exception;

    boolean saveImgFile(long articleId, Set<String> imgUrls);

    boolean del(Set<Long> ids);

    boolean clean();

    boolean update(FileUpdateDto updateDto);

    String download(Long id);

    PageInfo<FileVo> list(FileQueryDto queryDto);
}
