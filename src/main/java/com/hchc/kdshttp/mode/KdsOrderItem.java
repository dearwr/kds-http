package com.hchc.kdshttp.mode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public class KdsOrderItem implements Serializable {

    private long id;

    private String name;

    private String uuid = "";

    private long categoryId;

    private long dishId;

    private String operator = "";

    /**
     * 已完成数量
     */
    private int complete;

    /**
     * 退单数
     */
    private int refunded;

    private double count;

    private double amount;

    private List<KdsItemOption> options;

    private String operation = "";
    private String togoType;

    private String purpose = "";
    private int printed;

    private Date createTime = new Date();
    private boolean callup = false;

    private String remark = "";//备注

    private int reminder = 0;
    private boolean isHalf = false;
    private double refundCount;

    private String grade = "";
    private String orderNo = "";

    public KdsOrderItem() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getDishId() {
        return dishId;
    }

    public void setDishId(long dishId) {
        this.dishId = dishId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public int getRefunded() {
        return refunded;
    }

    public void setRefunded(int refunded) {
        this.refunded = refunded;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<KdsItemOption> getOptions() {
        return options;
    }

    public void setOptions(List<KdsItemOption> options) {
        this.options = options;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTogoType() {
        return togoType;
    }

    public void setTogoType(String togoType) {
        this.togoType = togoType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getPrinted() {
        return printed;
    }

    public void setPrinted(int printed) {
        this.printed = printed;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isCallup() {
        return callup;
    }

    public void setCallup(boolean callup) {
        this.callup = callup;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public boolean isHalf() {
        return isHalf;
    }

    public void setHalf(boolean half) {
        isHalf = half;
    }

    public double getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(double refundCount) {
        this.refundCount = refundCount;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
