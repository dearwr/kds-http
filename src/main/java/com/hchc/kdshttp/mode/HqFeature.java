package com.hchc.kdshttp.mode;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HqFeature {

    private long hqId;
    private long branchId;
    private String code;
    private String name;
    private String type;
    private String status;
    private Date createTime;
    private Date updateTime;
    private int value;

}
