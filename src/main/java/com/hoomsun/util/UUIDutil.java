package com.hoomsun.util;

import java.util.UUID;

public class UUIDutil {
    /**
     * 生成id
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
