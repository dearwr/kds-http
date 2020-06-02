package com.hchc.kdshttp.constant;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public enum RefundStatusEnum {

    WAIT_REFUND,
    DEFAULT,//未退款
    REJECT, //拒绝退款,
    REFUNDING, //退款中
    REFUNDED; //退款成功

    public static RefundStatusEnum valOf(String s) {
        for (RefundStatusEnum status : values()) {
            if (status.name().equals(s)) {
                return status;
            }
        }
        return null;
    }
}
