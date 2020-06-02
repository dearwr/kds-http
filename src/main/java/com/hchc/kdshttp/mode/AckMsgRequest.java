package com.hchc.kdshttp.mode;

import lombok.Data;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class AckMsgRequest {

    private List<String> msgIds;

}
