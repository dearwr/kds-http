package com.hchc.kdshttp.mode.response;

import lombok.Data;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class QueryMsg {

    private String msgId;
    private String orderNo;
    private String logAction;
    private String order;

}
