package com.hackyle.blog.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 友情链接
 */
@TableName(value ="tb_friend_link")
public class FriendLinkEntity implements Serializable {
    /**
     * ID：为了后续数据迁移，不使用自增主键，使用时间戳
     */
    @TableId
    private Long id;

    /**
     * 友链名称
     */
    private String name;

    /**
     * 链接
     */
    private String linkUrl;

    /**
     * 链接描述
     */
    private String description;

    /**
     * 友链图片地址
     */
    private String linkAvatarUrl;

    /**
     * 链接排序权重
     */
    private Integer rankWeight;

    /**
     * 创建时间: 年-月-日 时:分:秒
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除：0-false-未删除;1-true-已删除
     */
    @TableField("is_deleted")
    private Boolean deleted;

    @TableField("is_released")
    private Boolean released;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkAvatarUrl() {
        return linkAvatarUrl;
    }

    public void setLinkAvatarUrl(String linkAvatarUrl) {
        this.linkAvatarUrl = linkAvatarUrl;
    }

    public Integer getRankWeight() {
        return rankWeight;
    }

    public void setRankWeight(Integer rankWeight) {
        this.rankWeight = rankWeight;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }
}
