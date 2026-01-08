package com.hackyle.blog.customer.module.article.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.util.CrcUtils;
import com.hackyle.blog.customer.module.article.service.IdConfusionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ID混淆
 */
@Service
public class IdConfusionServiceImpl implements IdConfusionService {
    /**
     * 参与编码的字符
     * 如果要实现可读性，则在这里剔除这些字符：0、O、I、1
     * 如果只要大写字符，则在这里剔除小写字符即可
     */
    @Value("${article.id-confusion-base-char:0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz}")
    private String baseChar;

    /**
     * 定义Base62（0-9，a-z，A-Z）于字符数组，长度为62
     * 输入一个long型的num
     * 先模62，从Base62字符数组中取得一个字符
     * 再除62，直到num小于0时停止
     * 得到一个唯一串
     * 计算CRC8校验码
     */
    public String encode(long id) {
        if(id < 0) {
            throw new IllegalArgumentException("待编码的id必须不能小于0");
        }
        int baseSize = baseChar.length();
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int index = (int) (id % baseSize);
            sb.append(baseChar.charAt(index));
            id /= baseSize;
        }
        // 补足长度为8位
        //while (sb.length() < CODE_LENGTH) {
        //    sb.append('0'); // 也可填充随机字符
        //}

        //计算CRC8校验码
        String code = sb.reverse().toString();
        char crc8CheckSum = CrcUtils.crc8CheckSum(code);
        if(Character.isWhitespace(crc8CheckSum)) {
            throw new BizException("CRC8 Check Sum 计算失败");
        }

        return code + crc8CheckSum; // 高位在前
    }

    /**
     * 检查校验码
     * 依次遍历唯一串的每个字符c
     * 从字符数组中找到该个字符c所在的下标i
     * id = id*62+i
     * 最终解析出的值即为原始id
     */
    public long decode(String code) {
        String body = code.substring(0, code.length() - 1);
        char crc8CheckSum = code.charAt(code.length() - 1);
        if(!Objects.equals(crc8CheckSum, CrcUtils.crc8CheckSum(body))) {
            throw new BizException("CRC8 Check Sum 校验失败");
        }

        long id = 0;
        int baseSize = baseChar.length();

        for (char cc : body.toCharArray()) {
            Integer mod = null;
            for (int j = 0; j < baseSize; j++) {
                if(cc == baseChar.charAt(j)) {
                    mod = j;
                    break;
                }
            }
            if(mod == null) {
                throw new IllegalArgumentException("待解码的串中存在非法字符");
            }

            id = id * baseSize + mod;
        }

        return id;
    }

    public List<String> encode(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)) {
            throw new IllegalArgumentException("待编码的id必须不能为null");
        }
        List<String> encodeIds = new ArrayList<>(ids.size());

        for (Long id : ids) {
            if(id == null || id < 0) {
                throw new IllegalArgumentException("待编码的id必须不能为null或者小于0");
            }
            encodeIds.add(encode(id));
        }

        return encodeIds;
    }

    public List<Long> decode(List<String> ids) {
        if(CollectionUtil.isEmpty(ids)) {
            throw new IllegalArgumentException("待解码的id不能为null");
        }
        List<Long> decodeIds = new ArrayList<>(ids.size());

        for (String id : ids) {
            if(id == null) {
                throw new IllegalArgumentException("待解码的id不能为null");
            }
            decodeIds.add(decode(id));
        }
        return decodeIds;
    }
}
