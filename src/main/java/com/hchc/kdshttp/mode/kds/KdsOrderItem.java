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

    private double price;

}
