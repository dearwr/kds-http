package com.hchc.kdshttp.mode.kds;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class KdsItemOption implements Serializable {

    private String name;
    private int count;

}
