package com.hchc.kdshttp.constant;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public enum TogoEnum {

    // 美团
    MEITUAN("MEITUAN"),
    // 饿了么
    ELEME("ELEME");

    private String type;

    TogoEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getType(String type) {
        for (TogoEnum togoEnum : values()) {
            if (togoEnum.type.equals(type)) {
                return togoEnum.getType();
            }
        }
        return "";
    }

}
