package com.hchc.kdshttp.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.mode.request.AckMsg;
import com.hchc.kdshttp.mode.request.OrderStatus;
import com.hchc.kdshttp.mode.request.QueryUnit;
import com.hchc.kdshttp.mode.response.QueryMsg;
import com.hchc.kdshttp.pack.Result;
import com.hchc.kdshttp.service.KdsMsgService;
import com.hchc.kdshttp.service.OrderMsgService;
import com.hchc.kdshttp.task.TaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@RestController
@RequestMapping("/kds")
@Slf4j
public class MsgController {

    @Autowired
    private OrderMsgService orderMsgService;
    @Autowired
    private KdsMsgService kdsMsgService;

    /**
     * 轮询查订单
     *
     * @param branchId
     * @param uuid
     * @return
     */
    @GetMapping("loopQuery")
    public Result loopQuery(long branchId, String uuid) {
        log.info("[loopQuery] param branchId:{}, uuid:{}", branchId, uuid);
        if (branchId <= 0 || StringUtils.isEmpty(uuid)) {
            log.info("[loopQuery] param exist empty");
            return Result.fail("param exist empty");
        }
        List<QueryMsg> queryData;
        try {
            queryData = TaskManager.fetchQueryData(uuid);
            if (queryData != null) {
                TaskManager.removeQueryData(uuid);
                String[] msgIds = new String[queryData.size()];
                for (int i = 0; i < queryData.size(); i++) {
                    msgIds[i] = queryData.get(i).getMsgId();
                }
                log.info("[loopQuery] {} {} send query msgIds:{}", branchId, uuid, Arrays.toString(msgIds));
            }
            QueryUnit newUnit = new QueryUnit(branchId, uuid);
            TaskManager.tryRegisterQueryUnit(uuid, newUnit);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[loopQuery] happen error:{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok(queryData);
    }

    /**
     * 消息确认
     *
     * @param ackMsg
     * @return
     */
    @PostMapping("ackMsg")
    public Result ackMsg(@RequestBody AckMsg ackMsg) {
        log.info("[ackMsg] msgIds:{}", ackMsg.getMsgIds().toArray());
        if (CollectionUtils.isEmpty(ackMsg.getMsgIds())) {
            return Result.ok();
        }
        try {
            kdsMsgService.confirmMsg(ackMsg.getMsgIds());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[ackMsg] happen error:{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok();
    }

    /**
     * 改变订单状态
     *
     * @param orderStatus
     * @return
     */
    @PostMapping("changeStatus")
    public Result changeStatus(@RequestBody OrderStatus orderStatus) {
        log.info("[changeStatus] orderStatus:{}", JSON.toJSONString(orderStatus));
        if (orderStatus == null) {
            return Result.fail("param is empty");
        }
        try {
            orderMsgService.changeOrderStatus(orderStatus);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[changeStatus] happen error:{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok();
    }

    /**
     * 获取订单状态
     *
     * @param uuid
     * @param orderNo
     * @return
     */
    @GetMapping("/fetchOrderStatus")
    public Result fetchOrderStatus(String uuid, String orderNo) {
        log.info("[fetchOrderStatus] param orderNo:{}, uuid:{}", orderNo, uuid);
        KdsMessage newMsg = kdsMsgService.queryNewOrderMsg(uuid, orderNo);
        if (newMsg != null) {
            return Result.ok(newMsg.getData());
        }
        return Result.fail("not find order msg");
    }

}
