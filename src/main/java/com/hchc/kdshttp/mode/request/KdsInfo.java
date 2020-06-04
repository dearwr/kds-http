package com.hchc.kdshttp.mode.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
@NoArgsConstructor
public class KdsInfo {

    private int hqId;
    private int branchId;
    private String ip;
    private int port;
    private String deviceModeSetting;
    private String deviceType;
    private String deviceUUID;
    private String identifier;
    private String msg;
    private String versionCode;

}
