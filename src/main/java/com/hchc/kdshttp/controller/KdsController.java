package com.hchc.kdshttp.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.mode.request.KdsInfo;
import com.hchc.kdshttp.pack.Result;
import com.hchc.kdshttp.service.BranchKdsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@RestController
@RequestMapping("/kds")
@Slf4j
public class KdsController {

    @Autowired
    private BranchKdsService branchKdsService;

    /**
     * kds绑定
     *
     * @param kdsInfo
     * @return
     */
    @PostMapping("/bind")
    public Result bind(@RequestBody KdsInfo kdsInfo) {
        try {
            log.info("[bind] param :{}", JSON.toJSONString(kdsInfo));
            if (kdsInfo == null) {
                return Result.fail("param exit empty");
            }
            branchKdsService.bindKds(kdsInfo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[bind] happen error :{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok();
    }

    /**
     * kds解绑
     */
    @GetMapping("/unbind")
    public Result unBind(int branchId, String uuid) {
        log.info("[unBind] recv branchId:{}, uuid:{}", branchId, uuid);
        try {
            branchKdsService.unBindKds(branchId, uuid);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[unBind] happen error:{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

}
