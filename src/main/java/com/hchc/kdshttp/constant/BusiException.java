package com.hchc.kdshttp.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-11-09
 */
@Getter
@Setter
@AllArgsConstructor
public class BusiException extends Exception {

    private int code;
    private String message;

}
