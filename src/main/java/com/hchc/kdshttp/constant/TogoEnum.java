package com.hchc.kdshttp.constant;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public enum TogoEnum {

    MEITUAN("MEITUAN", "美团"),
    ELEME("ELEME", "饿了么");

    private String type;
    private String name;

    TogoEnum(String type, String name) {
        this.type = type;
        this.name = name;
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
