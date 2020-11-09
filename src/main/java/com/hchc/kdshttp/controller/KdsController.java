package com.hchc.kdshttp.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.constant.BusiException;
import com.hchc.kdshttp.dao.HqFeatureDao;
import com.hchc.kdshttp.mode.HqFeature;
import com.hchc.kdshttp.mode.request.KdsInfo;
import com.hchc.kdshttp.mode.response.WeChatQueueInfo;
import com.hchc.kdshttp.pack.Result;
import com.hchc.kdshttp.service.BranchKdsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Autowired
    private HqFeatureDao hqFeatureDao;

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
        } catch (BusiException e) {
            return new Result(String.valueOf(e.getCode()), e.getMessage(), null);
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


    @GetMapping("/wechat/queue/check")
    public Result checkWeChatQueue(int hqId, int branchId) {
        log.info("[checkWeChatQueue] recv hqId:{}, branchId:{}", hqId, branchId);
        try {
            WeChatQueueInfo weChatQueueInfo = new WeChatQueueInfo();
            List<HqFeature> hqFeatureList =  hqFeatureDao.queryWeChatQueueEnable(hqId);
            if (CollectionUtils.isEmpty(hqFeatureList) || "INVALID".equals(hqFeatureList.get(0).getStatus())) {
                log.info("[checkWeChatQueue] hqid:{}, 尚未开启外网版kds功能", hqId);
                return Result.ok(weChatQueueInfo);
            }
            weChatQueueInfo.setEnable(branchKdsService.queryWeChatQueueEnable(branchId));
            return Result.ok(weChatQueueInfo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[checkWeChatQueue] happen error:{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }



}
