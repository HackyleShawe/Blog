package com.hackyle.blog.admin.module.article.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FileUpdateDto {
    @NotNull
    private Long id;

}
