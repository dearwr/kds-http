package com.hchc.kdshttp.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public class StringUtil {

    private static AtomicInteger SOLO = new AtomicInteger();

    public static String generateMessageId(String flag){
        int countVal = CounterUtil.getCounter(SOLO, 10000);
        return String.format("%s%s%04d", DatetimeUtil.now(), flag, countVal);
    }

}
