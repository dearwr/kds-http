package com.hchc.kdshttp.mode.kds;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-08-11
 */
@Getter
@Setter
public class KdsMember {

    /**
     * 会员号
     */
    private String no;

    /**
     * 会员姓名
     */
    private String name;

    /**
     * 会员性别
     */
    private String sex;

    /**
     * 会员电话
     */
    private String phone;

}
