package com.hchc.kdshttp.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.mode.request.KdsInfo;
import com.hchc.kdshttp.pack.Result;
import com.hchc.kdshttp.service.BranchKdsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@RestController
@RequestMapping("/kds")
@Slf4j
public class KdsBindController {

    @Autowired
    private BranchKdsService branchKdsService;

    @PostMapping("/bind")
    public Result bind(@RequestBody KdsInfo kdsInfo) {
        log.info("[bind] param :{}", JSON.toJSONString(kdsInfo));
        if (kdsInfo == null) {
            return Result.fail("param exit empty");
        }
        try {
            branchKdsService.bindKds(kdsInfo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[bind] happen error :{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok();
    }
}
