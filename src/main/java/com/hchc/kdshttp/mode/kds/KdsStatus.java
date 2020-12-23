package com.hchc.kdshttp.mode.kds;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-12-07
 */
@Getter
@Setter
public class KdsStatus {

    private String type;
    private String name;
    private Date date;

}
