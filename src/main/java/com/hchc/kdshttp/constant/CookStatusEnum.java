package com.hchc.kdshttp.constant;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public enum CookStatusEnum {

    WAIT_COOK("WAIT_COOK", "ORDER_NEW", "待接单"),
    COOKING("COOKING", "ORDER_MAKE", "制作中"),
    COOKED("COOKED", "ORDER_TAKING", "制作完成"),
    PICKED("PICKED", "ORDER_COMPLETE", "取餐完成"),
    COMPLETE("COMPLETE", "ORDER_COMPLETE", "订单完成");

    private String status;
    private String callAction;
    private String name;

    CookStatusEnum(String status, String callAction, String name) {
        this.status = status;
        this.callAction = callAction;
        this.name = name;
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
