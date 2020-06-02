package com.hchc.kdshttp.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class KdsMessage {

    private int id;
    private int branchId;
    private String messageId;
    private String uuid;
    private String orderNo;
    private String logAction;
    private String data;
    private boolean pushed;
    private boolean valid;
    private Date createTime;
    private Date pushedTime;
    private Date invalidTime;
    private String type;

}
