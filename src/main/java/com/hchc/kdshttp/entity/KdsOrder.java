package com.hchc.kdshttp.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class KdsOrder {

    private int id;
    private int hqId;
    private int branchId;
    private String no;
    private String grade;
    private String type;
    private String logAction;
    private String data;
    private boolean completed;
    private Date createTime;
    private Date updateTime;

}
