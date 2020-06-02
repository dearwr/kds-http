package com.hchc.kdshttp.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public class CounterUtil {

    public static int getCounter(AtomicInteger counter, int max){
        if (counter.get() < max){
            int curr = counter.incrementAndGet();
            if (curr < max){
                return curr;
            }
            if (counter.compareAndSet(max, 1)){
                return 1;
            }
        }
        while (counter.get() >= max){
            continue;
        }
        return getCounter(counter, max);
    }
}
