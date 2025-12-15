package com.hackyle.blog.common.util;


/**
 * 唯一性标识符的生成与解析（基于long型和Base62的）
 */
public class IdConfusionUtils {
    /**
     * 参与编码的字符
     * 如果要实现可读性，则在这里剔除这些字符：0、O、I、1
     * 如果只要大写字符，则在这里剔除小写字符即可
     */
    private static final String BASE_CHAR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * 定义Base62（0-9，a-z，A-Z）于字符数组，长度为62
     * 输入一个long型的num
     * 先模62，从Base62字符数组中取得一个字符
     * 再除62，直到num小于0时停止
     * 得到一个唯一串
     */
    public static String encode(long id) {
        if(id < 0) {
            throw new IllegalArgumentException("待编码的id必须不能小于0");
        }
        int baseSize = BASE_CHAR.length();
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int index = (int) (id % baseSize);
            sb.append(BASE_CHAR.charAt(index));
            id /= baseSize;
        }
        // 补足长度为8位
        //while (sb.length() < CODE_LENGTH) {
        //    sb.append('0'); // 也可填充随机字符
        //}
        return sb.reverse().toString(); // 高位在前
    }

    /**
     * 依次遍历唯一串的每个字符c
     * 从字符数组中找到该个字符c所在的下标i
     * id = id*62+i
     * 最终解析出的值即为原始id
     */
    public static long decode(String code) {
        long id = 0;
        int baseSize = BASE_CHAR.length();

        for (char cc : code.toCharArray()) {
            Integer mod = null;
            for (int j = 0; j < baseSize; j++) {
                if(cc == BASE_CHAR.charAt(j)) {
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

    public static void main(String[] args) {
        long max = Long.MAX_VALUE;
        System.out.println(max);
        System.out.println(encode(max)); //AzL8n0Y58m7
        System.out.println(decode(encode(max)));

        long intMax = Integer.MAX_VALUE;
        System.out.println(intMax);
        System.out.println(encode(intMax)); //2LKcb1
        System.out.println(decode(encode(intMax)));

        decode("***da");
    }

}
