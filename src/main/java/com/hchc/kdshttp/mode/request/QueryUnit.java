package com.hchc.kdshttp.mode.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangrong
 * @date 2020-06-03
 */
@Data
@NoArgsConstructor
public class QueryUnit {

    private String branchId;

    private String uuid;

    public QueryUnit(String branchId, String uuid) {
        this.branchId = branchId;
        this.uuid = uuid;
    }
}
