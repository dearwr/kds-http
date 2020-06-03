package com.hchc.kdshttp.constant;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public enum CookStatusEnum {

    // 待接单
    WAIT_COOK("WAIT_COOK", "ORDER_NEW"),
    // 制作中
    COOKING("COOKING", "ORDER_MAKE"),
    // 制作完成
    COOKED("COOKED", "ORDER_TAKING"),
    // 取餐完成
    PICKED("PICKED", "ORDER_COMPLETE"),
    // 订单完成
    COMPLETE("COMPLETE", "ORDER_COMPLETE");

    private String status;
    private String callAction;

    CookStatusEnum(String status, String callAction) {
        this.status = status;
        this.callAction = callAction;
    }

    public static String getCallActionByStatus(String status) {
        for (CookStatusEnum cookEnum : values()) {
            if (cookEnum.status.equals(status)) {
                return cookEnum.callAction;
            }
        }
        return WAIT_COOK.callAction;
    }


}
