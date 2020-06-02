package com.hchc.kdshttp.mode;

import lombok.Data;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class OrderStatusRequest {

    private String uuid;

    private String orderNo;

    private String logAction;
}
