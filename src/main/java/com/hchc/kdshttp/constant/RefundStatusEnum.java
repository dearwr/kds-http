package com.hchc.kdshttp.constant;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public enum RefundStatusEnum {

    // 待退款
    WAIT_REFUND,
    //未退款
    DEFAULT,
    //拒绝退款
    REJECT,
    //退款中
    REFUNDING,
    //退款成功
    REFUNDED;

    public static RefundStatusEnum valOf(String s) {
        for (RefundStatusEnum status : values()) {
            if (status.name().equals(s)) {
                return status;
            }
        }
        return null;
    }
}
