package com.hchc.kdshttp.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class TKdsOrder {

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
    private Date makeTime;
    private Date callTime;
    private String bookTime;

}
