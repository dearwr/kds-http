package com.hchc.kdshttp.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.mode.request.AckMsg;
import com.hchc.kdshttp.mode.request.ChangeStatus;
import com.hchc.kdshttp.mode.request.OrderStatus;
import com.hchc.kdshttp.mode.response.QueryMsg;
import com.hchc.kdshttp.pack.Result;
import com.hchc.kdshttp.service.KdsMsgService;
import com.hchc.kdshttp.service.OrderMsgService;
import com.hchc.kdshttp.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @GetMapping("loopQuery")
    public Result loopQuery(String branchId, String uuid) {
        log.info("[loopQuery] param branchId:{}, uuid:{}", branchId, uuid);
        if (StringUtils.isEmpty(branchId) || StringUtils.isEmpty(uuid)) {
            log.info("[loopQuery] param exist empty");
            return Result.fail("param exist empty");
        }
        Date endTime = new Date();
        Date startTime = DatetimeUtil.dayBegin(endTime);
        QueryMsg queryMsg;
        List<QueryMsg> queryMsgList;
        List<String> msgIds;
        try {
            List<KdsMessage> messages = kdsMsgService.queryUnPushedMsg(branchId, uuid, startTime, endTime, -1);
            queryMsgList = new ArrayList<>(messages.size());
            msgIds = new ArrayList<>(messages.size());
            for (KdsMessage msg : messages) {
                queryMsg = new QueryMsg();
                msgIds.add(msg.getMessageId());
                queryMsg.setMsgId(msg.getMessageId());
                queryMsg.setOrderNo(msg.getOrderNo());
                queryMsg.setLogAction(msg.getLogAction());
                queryMsg.setOrder(msg.getData());
                queryMsgList.add(queryMsg);
            }
            log.info("[loopQuery] send result msgIds:{}", JSON.toJSONString(msgIds));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[loopQuery] happen error :{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok(queryMsgList);
    }

    @PostMapping("ackMsg")
    public Result ackMsg(@RequestBody AckMsg ackMsg) {
        log.info("[ackMsg] msgIds:{}", ackMsg.getMsgIds().toArray());
        try {
            boolean suc = kdsMsgService.confirmMsg(ackMsg.getMsgIds());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[ackMsg] happen error:{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok();
    }

    @PostMapping("changeStatus")
    public Result changeStatus(ChangeStatus changeStatus) {
        List<OrderStatus> orderStatusList = changeStatus.getOrderStatuses();
        log.info("[changeStatus] orderStatusList:{}", JSON.toJSONString(orderStatusList));
        if (CollectionUtils.isEmpty(orderStatusList)) {
            return Result.ok();
        }
        try {
            for (OrderStatus orderStatus : orderStatusList) {
                orderMsgService.changeOrderStatus(orderStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[changeStatus] happen error:{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok();
    }

}
