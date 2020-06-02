package com.hchc.kdshttp.mode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
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

    public String getCallAction() {
        return callAction;
    }

    public void setCallAction(String callAction) {
        this.callAction = callAction;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOperationFlowNo() {
        return operationFlowNo;
    }

    public void setOperationFlowNo(String operationFlowNo) {
        this.operationFlowNo = operationFlowNo;
    }

    public String getParentOrderNo() {
        return parentOrderNo;
    }

    public void setParentOrderNo(String parentOrderNo) {
        this.parentOrderNo = parentOrderNo;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTogoType() {
        return togoType;
    }

    public void setTogoType(String togoType) {
        this.togoType = togoType;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPreGrade() {
        return preGrade;
    }

    public void setPreGrade(String preGrade) {
        this.preGrade = preGrade;
    }

    public int getGradeVersion() {
        return gradeVersion;
    }

    public void setGradeVersion(int gradeVersion) {
        this.gradeVersion = gradeVersion;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getKitchenDeviceName() {
        return kitchenDeviceName;
    }

    public void setKitchenDeviceName(String kitchenDeviceName) {
        this.kitchenDeviceName = kitchenDeviceName;
    }

    public String getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(String deliverTime) {
        this.deliverTime = deliverTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public List<KdsOrderItem> getItems() {
        return items;
    }

    public void setItems(List<KdsOrderItem> items) {
        this.items = items;
    }

    public int getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public boolean isPrint() {
        return print;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }
}
