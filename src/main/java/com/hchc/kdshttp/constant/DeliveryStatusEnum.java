package com.hchc.kdshttp.constant;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public enum DeliveryStatusEnum {

    NEW("NEW", "ORDER_NEW"),      // 新订单
    DEFAULT("DEFAULT", "ORDER_MAKE"), // 等待配送
    DELIVERYING("DELIVERYING", "ORDER_DELIVERYING"),  //配送中
    DELIVERYED("DELIVERYED", "ORDER_COMPLETE");  // 配送成功

    private String status;
    private String callAction;

    DeliveryStatusEnum(String status, String callAction) {
        this.status = status;
        this.callAction = callAction;
    }

    public static String getCallActionByStatus(String status) {
        if (status == null || "".equals(status)) {
            return NEW.callAction;
        }
        for (DeliveryStatusEnum deliveryEnum : values()) {
            if (deliveryEnum.status.equals(status)) {
                return deliveryEnum.callAction;
            }
        }
        return null;
    }
}
