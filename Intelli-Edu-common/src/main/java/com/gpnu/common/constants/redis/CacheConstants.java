package com.gpnu.common.constants.redis;

import java.util.concurrent.TimeUnit;


public class CacheConstants {

    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;

    /**
     * 缓存时间单位，默认分钟
     */
    public final static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;


    public static final String COLON = ":";
}
