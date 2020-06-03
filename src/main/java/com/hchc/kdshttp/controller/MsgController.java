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
import java.util.Arrays;
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
        Date startTime = DatetimeUtil.dayBegin(new Date());
        List<QueryMsg> queryMsgList;
        String[] msgIds;
        QueryMsg queryMsg;
        try {
            List<KdsMessage> messages = kdsMsgService.queryUnPushedMsg(branchId, uuid, startTime, -1);
            queryMsgList = new ArrayList<>(messages.size());
            msgIds = new String[messages.size()];
            for (int i = 0; i < messages.size(); i++) {
                queryMsg = new QueryMsg();
                msgIds[i] = messages.get(i).getMessageId();
                queryMsg.setMsgId(messages.get(i).getMessageId());
                queryMsg.setOrderNo(messages.get(i).getOrderNo());
                queryMsg.setLogAction(messages.get(i).getLogAction());
                queryMsg.setOrder(messages.get(i).getData());
                queryMsgList.add(queryMsg);
            }
            log.info("[loopQuery] send {} result msgIds:{}", uuid, Arrays.toString(msgIds));
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

    @PostMapping("changeStatus")
    public Result changeStatus(@RequestBody ChangeStatus changeStatus) {
        log.info("[changeStatus] orderStatuses:{}", JSON.toJSONString(changeStatus.getOrderStatuses()));
        if (CollectionUtils.isEmpty(changeStatus.getOrderStatuses())) {
            return Result.ok();
        }
        try {
            for (OrderStatus orderStatus : changeStatus.getOrderStatuses()) {
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
