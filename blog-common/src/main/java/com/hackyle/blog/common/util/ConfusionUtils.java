package com.hackyle.blog.common.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ID混淆(confusion)工具类
 * 主要思路：将ID进行对称加密，再转换为Base64串
 *
 * Base64的定义：用 64 个可打印字符来表示二进制数据，小写字母 a-z、大写字母 A-Z、数字 0-9、符号"+"、"/"（当原始数据凑不够三个字节时，编码结果中会使用额外的符号'='，实际上是 65 个字符）。
 *
 * 注意：
 *  混淆规则不应该轻易改动
 *  由于混淆后的ID可能会用作URL的一部分，所以直接转换为URL类型的Base64串（或者替换掉常规Base64中的不符合URL规则的字符）
 *  由于混淆后的ID可能作为HTML标签的ID属性值（只能包含字母、数字、下划线和连字符'-'），所以需要替换Base64中除了那三种的字符
 */
public class ConfusionUtils {
    private static final String DEFAULT_PW = "blog.hackyle.com:HackyleBlogPW";

    public static long timestampID() {
        return System.currentTimeMillis();
    }

    public static List<String> encode(List<Long> sourceIdList) {
        List<String> encryptedIdList = new ArrayList<>(sourceIdList.size());

        for (Long id : sourceIdList) {
            encryptedIdList.add(encode(id));
        }

        return encryptedIdList;
    }

    public static List<Long> decode(List<String> sourceIdList) {
        List<Long> decryptIdList = new ArrayList<>(sourceIdList.size());

        for (String id : sourceIdList) {
            decryptIdList.add(decode(id));
        }
        return decryptIdList;
    }


    /**
     * 加密
     * @param sourceObj 加密前的ID为Long
     * @param targetObj 加密后的ID为String
     */
    public static void encode(Object sourceObj, Object targetObj) {
        encode(sourceObj, "id", targetObj, "setId");
    }

    /**
     * 加密
     * @param sourceObj
     * @param sourceField 指定加密的字段
     * @param targetObj
     * @param targetMethod 指定加密后赋值给targetObj调用的方法
     */
    public static void encode(Object sourceObj, String sourceField, Object targetObj, String targetMethod) {
        if(sourceObj == null) {
            throw new RuntimeException("encrypt: source obj can't be null.");
        }
        if(targetObj == null) {
            throw new RuntimeException("encrypt: target obj can't be null.");
        }

        sourceField = StringUtils.isBlank(sourceField) ? "id" : sourceField;
        targetMethod = StringUtils.isBlank(targetMethod) ? "setId" : targetMethod;

        try {
            Field sourceIdField = sourceObj.getClass().getDeclaredField(sourceField);
            sourceIdField.setAccessible(true);
            Object id = sourceIdField.get(sourceObj);
            sourceIdField.setAccessible(false);

            //加密
            String encryptedId = encode(Long.parseLong(id.toString()));

            Method targetIdField = targetObj.getClass().getMethod(targetMethod, String.class);
            targetIdField.invoke(targetObj, encryptedId);
        } catch (Exception e) {
            throw new RuntimeException("encrypt id has been occurred exception.", e);
        }
    }

    /**
     * 解密
     * @param sourceObj 解密前ID为String
     * @param targetObj 解密后ID为Long
     */
    public static void decode(Object sourceObj, Object targetObj) {
        decode(sourceObj, "id", targetObj, "setId");
    }

    /**
     * 解密
     * @param sourceObj
     * @param sourceField 指定解密的字段
     * @param targetObj
     * @param targetMethod 指定解密后赋值给targetObj调用的方法
     */
    public static void decode(Object sourceObj, String sourceField, Object targetObj, String targetMethod) {
        if(sourceObj == null) {
            throw new RuntimeException("decrypt: source obj can't be null.");
        }
        if(targetObj == null) {
            throw new RuntimeException("decrypt: target obj can't be null.");
        }

        sourceField = StringUtils.isBlank(sourceField) ? "id" : sourceField;
        targetMethod = StringUtils.isBlank(targetMethod) ? "setId" : targetMethod;

        try {
            Field sourceIdField = sourceObj.getClass().getDeclaredField("id");
            sourceIdField.setAccessible(true);
            Object id = sourceIdField.get(sourceObj);
            sourceIdField.setAccessible(false);

            //解密
            long decryptedId = decode(id.toString());

            Method targetIdField = targetObj.getClass().getMethod("setId", Long.class);
            targetIdField.invoke(targetObj, decryptedId);
        } catch (Exception e) {
            throw new RuntimeException("decrypt id has been occurred exception.", e);
        }
    }

    /**
     * 加密
     * 强制要求: sourceObjList与targetObjList的下标一一对应
     * @param sourceObjList 加密前的ID为Long
     * @param targetObjList 加密后的ID为String
     */
    public static void batchEncode(List<?> sourceObjList, List<?> targetObjList) {
        batchEncode(sourceObjList, "id", targetObjList, "setId");
    }

    /**
     * 加密
     * @param sourceObjList
     * @param sourceField
     * @param targetObjList
     * @param targetMethod
     */
    public static void batchEncode(List<?> sourceObjList, String sourceField, List<?> targetObjList, String targetMethod) {
        if(sourceObjList == null || sourceObjList.size() < 1) {
            return;
        }
        if(targetObjList == null || targetObjList.size() <1) {
            throw new RuntimeException("encrypt batch: targetObjList can't be null or empty.");
        }
        if(sourceObjList.size() != targetObjList.size()) {
            throw new RuntimeException("encrypt batch: sourceObjList and targetObjList size must be equal.");
        }

        sourceField = StringUtils.isBlank(sourceField) ? "id" : sourceField;
        targetMethod = StringUtils.isBlank(targetMethod) ? "setId" : targetMethod;

        for (int i = 0, len=sourceObjList.size(); i < len; i++) {
            Object sourceObj = sourceObjList.get(i);
            Object targetObj = targetObjList.get(i);
            encode(sourceObj, sourceField, targetObj, targetMethod);
        }
    }

    /**
     * 解密
     * 强制要求: sourceObjList与targetObjList的下标一一对应
     * @param sourceObjList 解密前ID为String
     * @param targetObjList 解密后ID为Long
     */
    public static void batchDecode(List<?> sourceObjList, List<?> targetObjList) {
        batchDecode(sourceObjList, "id", targetObjList, "setId");
    }

    /**
     * 解密
     * @param sourceObjList
     * @param sourceField
     * @param targetObjList
     * @param targetMethod
     */
    public static void batchDecode(List<?> sourceObjList, String sourceField, List<?> targetObjList, String targetMethod) {
        if(sourceObjList == null || sourceObjList.size() < 1) {
            return;
        }
        if(targetObjList == null || targetObjList.size() <1) {
            throw new RuntimeException("decrypt batch: targetObjList can't be null or empty.");
        }

        if(sourceObjList.size() != targetObjList.size()) {
            throw new RuntimeException("decrypt batch: sourceObjList and targetObjList size must be equal.");
        }

        sourceField = StringUtils.isBlank(sourceField) ? "id" : sourceField;
        targetMethod = StringUtils.isBlank(targetMethod) ? "setId" : targetMethod;

        for (int i = 0, len=sourceObjList.size(); i < len; i++) {
            Object sourceObj = sourceObjList.get(i);
            Object targetObj = targetObjList.get(i);
            decode(sourceObj, sourceField, targetObj, targetMethod);
        }
    }

    /**
     * ID混淆：AES加密，转换为Base64串，特殊字符处理
     */
    public static String encode(long id) {
        String encrypt = AESUtils.encryptAndToBase64(String.valueOf(id), DEFAULT_PW);

        //混淆后的ID可能作为HTML标签的ID属性值（只能包含字母、数字、下划线和连字符'-'），所以需要替换Base64中除了那三种的字符
        encrypt = encrypt.replaceAll("\\+", "-")
                .replaceAll("/", "_");
        int count = 0; //替换Base64串后面可能存在的=。有几个=，就将其替换后，在尾巴后添加数字
        for (int i = encrypt.length()-1; i >= 0; i--) {
            if('=' != encrypt.charAt(i)) { //=只可能出现在Base64串的最后面
                encrypt = encrypt.substring(0, i+1); //移除Base64串最后面的=
                break;
            }
            count++;
        }
        encrypt += count;

        return encrypt;
    }

    /**
     * ID解混淆：特殊字符恢复处理，Base64转换，AES解密
     */
    public static long decode(String encryptedId) {
        if(StringUtils.isBlank(encryptedId)) {
            throw new RuntimeException("encryptedId为空");
        }

        encryptedId = encryptedId.replaceAll("-", "+")
                .replaceAll("_", "/");
        char count = encryptedId.charAt(encryptedId.length()-1); //最后一个数字代表Base64串中有几个=，需要在尾巴后添加=
        int cc = Integer.parseInt(count + "");
        encryptedId = encryptedId.substring(0, encryptedId.length()-1);
        for (int i = 0; i < cc; i++) {
            encryptedId += "=";
        }

        String decryptedId = AESUtils.decryptAndToBase64(encryptedId, DEFAULT_PW);
        return Long.parseLong(decryptedId);
    }

    //
    //public static void main(String[] args) throws Exception {
    //    long start = timestampID();
    //    for (int i = 0; i < 10; i++) {
    //        testAES();
    //    }
    //    System.out.println(timestampID() - start);
    //}
    //
    //private static void testAES() {
    //    long id = System.currentTimeMillis() + new Random().nextLong(1000);
    //    System.out.println(id);
    //    String encryptByAES = encode(id);
    //    System.out.println(encryptByAES);
    //
    //    long decryptByAES = decode(encryptByAES);
    //    System.out.println(decryptByAES);
    //}
}
