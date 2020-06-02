package com.hchc.kdshttp.constant;

import java.util.*;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public enum ActionEnum {
    // 新订单
    ORDER_NEW("ORDER_NEW", "ORDER_NEW", CmdType.SYNC_NEW_REFUND),
    // 预点单
    ORDER_PRE("ORDER_PRE", "ORDER_PRE", CmdType.SYNC_NEW_REFUND),
    // 制作中
    ORDER_MAKE("ORDER_MAKE", "ORDER_MAKE", CmdType.SYNC_COOK_STATUE),
    // 制作完成
    ORDER_TAKING("ORDER_TAKING", "ORDER_CALL", CmdType.SYNC_COOK_STATUE),
    // 订单配送中
    ORDER_DELIVERYING("ORDER_DELIVERYING", "ORDER_DELIVERYING", CmdType.SYNC_COOK_STATUE),
    // 取餐完成
    ORDER_COMPLETE("ORDER_COMPLETE", "TAKE_COMPLETE", CmdType.SYNC_COOK_STATUE),
    // 已退单
    ORDER_REFUND("ORDER_REFUND", "ORDER_REFUND", CmdType.SYNC_NEW_REFUND);

    private String callAction;
    private String logAction;
    private int cmd;

    ActionEnum(String callAction, String logAction, int cmd) {
        this.callAction = callAction;
        this.logAction = logAction;
        this.cmd = cmd;
    }

    public String getLogAction() {
        return logAction;
    }

    public String getCallAction() {
        return callAction;
    }

    public int getCmd() {
        return cmd;
    }

    public static String getLogActionByCallAction(String callAction) {
        for (ActionEnum action : values()) {
            if (action.getCallAction().equals(callAction)) {
                return action.getLogAction();
            }
        }
        return ORDER_NEW.getCallAction();
    }

    public static String getCallActionByLogAction(String logAction) {
        for (ActionEnum action : values()) {
            if (action.getLogAction().equals(logAction)) {
                return action.getCallAction();
            }
        }
        return ORDER_NEW.getCallAction();
    }

    public static int getCmdByLogAction(String logAction) {
        for (ActionEnum action : values()) {
            if (action.getLogAction().equals(logAction)) {
                return action.getCmd();
            }
        }
        return -1;
    }

    /**
     * 中间状态logAction
     */
    public static final List<String> MID_STATUS_ACTION_LIST = new ArrayList<>();

    static {
        MID_STATUS_ACTION_LIST.add("ORDER_MAKE");
        MID_STATUS_ACTION_LIST.add("ORDER_CALL");
    }

    /**
     * 有效的新logAction
     */
    public static final Map<String, List<String>> VALID_LOG_ACTION_MAP = new HashMap<>();

    static {
        VALID_LOG_ACTION_MAP.put("ORDER_NEW", Arrays.asList("ORDER_MAKE", "ORDER_CALL", "TAKE_COMPLETE", "ORDER_REFUND", "ORDER_DELIVERYING"));
        VALID_LOG_ACTION_MAP.put("ORDER_PRE", Arrays.asList("ORDER_MAKE", "ORDER_CALL", "TAKE_COMPLETE", "ORDER_REFUND", "ORDER_DELIVERYING"));
        VALID_LOG_ACTION_MAP.put("ORDER_MAKE", Arrays.asList("ORDER_CALL", "TAKE_COMPLETE", "ORDER_REFUND", "ORDER_DELIVERYING"));
        VALID_LOG_ACTION_MAP.put("ORDER_CALL", Arrays.asList("TAKE_COMPLETE", "ORDER_REFUND", "ORDER_DELIVERYING"));
        VALID_LOG_ACTION_MAP.put("ORDER_DELIVERYING", Arrays.asList("TAKE_COMPLETE", "ORDER_REFUND"));
        VALID_LOG_ACTION_MAP.put("TAKE_COMPLETE", Collections.singletonList(("ORDER_REFUND")));
    }

    /**
     * 无效的旧logAction
     */
    public static final Map<String, List<String>> INVALID_LOG_ACTION_MAP = new HashMap<>();

    static {
        INVALID_LOG_ACTION_MAP.put("ORDER_CALL", Collections.singletonList("ORDER_MAKE"));
        INVALID_LOG_ACTION_MAP.put("ORDER_DELIVERYING", Arrays.asList("ORDER_MAKE", "ORDER_CALL"));
        INVALID_LOG_ACTION_MAP.put("TAKE_COMPLETE", Arrays.asList("ORDER_CALL", "ORDER_MAKE", "ORDER_DELIVERYING"));
        INVALID_LOG_ACTION_MAP.put("ORDER_REFUND", Arrays.asList("TAKE_COMPLETE", "ORDER_CALL", "ORDER_MAKE", "ORDER_DELIVERYING"));
    }

}
