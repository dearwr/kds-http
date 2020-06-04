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

    private long branchId;

    private String uuid;

    public QueryUnit(long branchId, String uuid) {
        this.branchId = branchId;
        this.uuid = uuid;
    }
}
