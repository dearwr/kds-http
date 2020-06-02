package com.hchc.kdshttp.entity;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public class TBranchKds {

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHqId() {
        return hqId;
    }

    public void setHqId(int hqId) {
        this.hqId = hqId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getOffLineTime() {
        return offLineTime;
    }

    public void setOffLineTime(Date offLineTime) {
        this.offLineTime = offLineTime;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
