package com.hchc.kdshttp.mode.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
@NoArgsConstructor
public class OrderStatus {

    private String uuid;

    private String orderNo;

    private String logAction;

}
