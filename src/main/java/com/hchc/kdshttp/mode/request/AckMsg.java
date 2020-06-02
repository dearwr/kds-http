package com.hchc.kdshttp.mode.request;

import lombok.Data;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
public class AckMsg {

    private List<String> msgIds;

}
