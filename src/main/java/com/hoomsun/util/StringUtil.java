package com.hoomsun.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 */
public class StringUtil extends StringUtils {

    /**
     * 子字符串出现的个数
     */
    public static int getSubStrCount(String str, String subStr) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(subStr, index)) != -1) {
            index = index + subStr.length();
            count++;
        }
        return count;
    }
}
