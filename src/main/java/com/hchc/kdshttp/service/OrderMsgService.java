package com.hchc.kdshttp.service;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.constant.ActionEnum;
import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.dao.KdsOrderDao;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.entity.TKdsOrder;
import com.hchc.kdshttp.mode.kds.KdsOrder;
import com.hchc.kdshttp.mode.request.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Service
@Slf4j
public class OrderMsgService {

    @Autowired
    private KdsMsgService kdsMsgService;

    @Autowired
    private KdsMsgDao kdsMsgDao;

    @Autowired
    private KdsOrderDao kdsOrderDao;

    /**
     * 根据订单号更新订单状态并生成消息
     *
     * @param order
     * @return
     */
    public void changeOrderStatus(OrderStatus order) {
        TKdsOrder tOrder = kdsOrderDao.query(order.getOrderNo());
        if (tOrder == null) {
            log.info("[getMsgByOrderNo] not find order :{}", order.getOrderNo());
            return;
        }
        updateOrder(order.getUuid(), tOrder, order.getLogAction());
    }

    private void updateOrder(String uuid, TKdsOrder tOrder, String newLogAction) {
        String oldLogAction = tOrder.getLogAction();
        // 检查传入的新状态是否有效
        List<String> validLogActions = ActionEnum.VALID_LOG_ACTION_MAP.get(oldLogAction);
        if (validLogActions != null && validLogActions.contains(newLogAction)) {
            KdsOrder kdsOrder = JSON.parseObject(tOrder.getData(), KdsOrder.class);
            if (ActionEnum.ORDER_COMPLETE.getLogAction().equals(newLogAction)) {
                tOrder.setCompleted(true);
            }
            kdsOrder.setCallAction(ActionEnum.getCallActionByLogAction(newLogAction));
            tOrder.setData(JSON.toJSONString(kdsOrder));
            tOrder.setLogAction(newLogAction);
            tOrder.setUpdateTime(new Date());
            kdsOrderDao.updateOrder(tOrder);
            createMsg(uuid, tOrder);
        } else {
            log.info("[updateOrder] {} check logAction is valid, oldAction:{}, newAction:{}", tOrder.getNo(), oldLogAction, newLogAction);
            return;
        }
    }

    private void createMsg(String uuid, TKdsOrder tOrder) {
        if (ActionEnum.INVALID_LOG_ACTION_MAP.get(tOrder.getLogAction()) != null) {
            List<String> invalidLogActions = new ArrayList<>(ActionEnum.INVALID_LOG_ACTION_MAP.get(tOrder.getLogAction()));
            kdsMsgDao.updateInvalidMsg(tOrder.getNo(), invalidLogActions);
        }
        List<KdsMessage> messages = kdsMsgService.createMsgByOrder(tOrder, null, uuid);
        kdsMsgDao.batchAdd(messages);
    }

}
