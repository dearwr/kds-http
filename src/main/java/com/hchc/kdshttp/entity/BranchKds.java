package com.hchc.kdshttp.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class BranchKds {

    private int id;
    private int hqId;
    private int branchId;
    private String name;
    private String uuid;
    private boolean open;
    private Date createTime;
    private Date offLineTime;
    private Date onlineTime;
    private String version;

}
