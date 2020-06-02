package com.hchc.kdshttp.constant;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public class CmdType {

    /**
     * 创建连接
     */
    public static final int CREATE_CONNECT = 602;
    /**
     * 创建连接确认
     */
    public static final int CONNECT_ACK = 660;
    /**
     * 同步新订单/退单
     */
    public static final int SYNC_NEW_REFUND = 620;
    /**
     * 同步订单制作状态 COOKED/COMPLETE
     */
    public static final int SYNC_COOK_STATUE = 662;
    /**
     * 同步订单确认
     */
    public static final int SYNC_ORDER_ACK = 624;
    /**
     * 普通消息确认
     */
    public static final int NORMAL_MESSAGE_ACK = 603;
    /**
     * kds的log上传
     */
    public static final int KDS_LOG_UPLOAD = 650;
    /**
     * kds的log上传确认
     */
    public static final int KDS_LOG_UPLOAD_ACK = 651;
    /**
     * 心跳
     */
    public static final int HEART_BEAT = 606;
    /**
     * 心跳确认
     */
    public static final int HEART_BEAT_ACK = 600;
    /**
     * 检查订单状态
     */
    public static final int CHECK_ORDER_STATUS = 666;


}
