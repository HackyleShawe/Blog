package com.hackyle.blog.customer.module.website.model.dto;

import com.hackyle.blog.common.constant.RegexPatternConstants;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class FeedbackAddDto {
    /**
     * 留言者称呼
     */
    @NotBlank(message = "留言者称呼不能为空")
    private String name;
    /**
     * 留言者邮箱
     */
    @Email(message = "邮箱格式不合法")
    private String email;
    /**
     * 留言者电话
     */
    @Pattern(regexp = RegexPatternConstants.PHONE_REGEXP, message = "手机号格式不合法")
    private String phone;
    /**
     * 留言者的链接（个人主页、博客页）
     */
    //@URL(message = "URL格式不合法") 强制要求：协议://domain.二级域
    private String link;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 状态：0-无效 1-有效
     * @see com.hackyle.blog.common.enums.StatusEnum
     */
    private Boolean status;
}
