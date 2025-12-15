package com.hackyle.blog.admin.module.monitor.model.vo;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;

/**
 * 內存相关信息
 *
 * @author valarchie
 */
@Data
public class MemoryInfoVo {

    private static final int KB = 1024;

    private static final int MB = KB * 1024;

    private static final int GB = MB * 1024;


    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;


    public double getTotal() {
        return NumberUtil.div(total, GB, 2);
    }

    public double getUsed() {
        return NumberUtil.div(used, GB, 2);
    }

    public double getFree() {
        return NumberUtil.div(free, GB, 2);
    }

    public double getUsage() {
        return NumberUtil.div(used * 100, total, 2);
    }
}
