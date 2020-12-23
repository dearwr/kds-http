package com.hchc.kdshttp.mode.kds;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-08-11
 */
@Getter
@Setter
public class KdsCourier {

    /**
     * 外送公司
     */
    private String company = "";

    /**
     * 骑手姓名
     */
    private String name = "";

    /**
     * 骑手电话
     */
    private String phone = "";

    /**
     * 骑手状态
     */
    private String status = "";

    /**
     * 平台单号
     */
    private String no = "";
}
