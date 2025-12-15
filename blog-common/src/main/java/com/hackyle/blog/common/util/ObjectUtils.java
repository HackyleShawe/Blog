package com.hackyle.blog.common.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;

/**
 * 对象操作工具类
 */
public class ObjectUtils {

    /**
     * 判断对象是否为空对象，空对象表示本身为null或者所有属性都为null ，
     * String类型的对象会判断是否为空字符串
     * 此方法不判断static属性
     *
     * @param obj             Bean对象
     * @param ignoreFiledNames 忽略检查的字段名
     * @return 是否为空，<code>true</code> - 空 / <code>false</code> - 非空
     */
    public static boolean isEmpty(Object obj, String... ignoreFiledNames) {
        if (null != obj) {
            for (Field field : ReflectUtil.getFields(obj.getClass())) {
                if (ModifierUtil.isStatic(field)) {
                    continue;
                }
                Object fieldValue = ReflectUtil.getFieldValue(obj, field);
                if ((!ArrayUtil.contains(ignoreFiledNames, field.getName())) && null != fieldValue) {
                    if (ReflectUtil.getFieldValue(obj, field) instanceof String) {
                        if (StrUtil.isNotBlank((String) fieldValue)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
