package com.hchc.kdshttp.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.dao.KdsOrderDao;
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
    @Autowired
    private KdsOrderDao kdsOrderDao;

    /**
     * 轮询查订单
     *
     * @param branchId
     * @param uuid
     * @return
     */
    @GetMapping("loopQuery")
    public Result loopQuery(long branchId, String uuid) {
        log.info("[loopQuery] recv branchId:{}, uuid:{}", branchId, uuid);
        if (branchId <= 0 || StringUtils.isEmpty(uuid)) {
            log.info("[loopQuery] param exist empty");
            return Result.fail("param exist empty");
        }
        List<QueryMsg> queryData;
        try {
            queryData = TaskManager.fetchData(uuid);
            if (queryData != null) {
                TaskManager.removeData(uuid);
                String[] msgIds = new String[queryData.size()];
                for (int i = 0; i < queryData.size(); i++) {
                    msgIds[i] = queryData.get(i).getMsgId();
                }
                log.info("[loopQuery] send {} {} query msgIds:{}", branchId, uuid, Arrays.toString(msgIds));
            }
            QueryUnit newUnit = new QueryUnit(branchId, uuid);
            TaskManager.tryRegisterQuery(uuid, newUnit);
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
        log.info("[ackMsg] recv msgIds:{}", ackMsg.getMsgIds().toArray());
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
        log.info("[changeStatus] recv orderStatus:{}", JSON.toJSONString(orderStatus));
        if (orderStatus == null) {
            return Result.fail("param is empty");
        }
        try {
            orderMsgService.changeOrderStatus(orderStatus);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[changeStatus] happen error:{}", e.getMessage());
            return Result.ok(e);
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
        log.info("[fetchOrderStatus] recv orderNo:{}, uuid:{}", orderNo, uuid);
        KdsMessage newMsg;
        try {
            newMsg = kdsMsgService.queryOrderNewMsg(uuid, orderNo);
            if (newMsg == null) {
                return Result.fail("not find order msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[fetchOrderStatus] happen error:{}", e.getMessage());
            return Result.fail(e);
        }
        return Result.ok((Object) newMsg.getData());
    }

    /**
     * 重复叫号
     *
     * @param uuid
     * @param orderNo
     * @return
     */
    @GetMapping("/repeatCall")
    public Result repeatCall(String uuid, String orderNo) {
        log.info("[repeatCall] recv uuid:{}, orderNo:{}", uuid, orderNo);
        try {
            kdsOrderDao.updateCallTime(orderNo);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[repeatCall] happen error:{}", e.getMessage());
            return Result.fail(e);
        }
    }

    /**
     * 根据订单类型批量完成订单
     */
    @GetMapping("/order/batchComplete")
    public Result batchComplete(int hqId, int branchId, String types) {
        log.info("[batchComplete] recv hqId:{}, branchId:{}, types:{}", hqId, branchId, types);
        if (StringUtils.isEmpty(types)) {
            return Result.fail("param 'types' is empty");
        }
        try {
            orderMsgService.batchCompleteByTypes(hqId, branchId, types);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[batchComplete] happen error:{}", e.getMessage());
            return Result.fail(e);
        }
    }

}
