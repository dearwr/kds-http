package com.hchc.kdshttp.mode;

import java.io.Serializable;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public class KdsItemOption implements Serializable {

    private String name;
    private int count;

    public KdsItemOption() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
