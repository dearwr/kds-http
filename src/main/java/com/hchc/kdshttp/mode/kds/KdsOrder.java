package com.hchc.kdshttp.mode.kds;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class KdsOrder implements Serializable {

    private String callAction;

    private long id;

    private String orderNo;

    private String operationFlowNo;

    private String parentOrderNo;

    private String operator;

    private String grade;   // 外卖给空

    private String togoType;

    private String sn;  // 外卖号牌

    private String preGrade = "";

    private int gradeVersion;

    private String location;

    private String kitchenDeviceName = "";

    private String deliverTime = "";

    private Date updateTime = new Date();

    private String seq;

    private int callCount = 0;

    private List<KdsOrderItem> items;

    private int parentPosition = 0;

    private boolean isRefresh = false;

    private boolean print;

    private String remark;

    private KdsCourier courier;

    private KdsMember member;

    private List<KdsStatus> status;

    private KdsCustomer customer;

    private KdsStatus lastStatus;

    private double price;

    private double actualPrice;

    private String platformNo = "";

    private double goodsCount;

    private int wmversion;

}
