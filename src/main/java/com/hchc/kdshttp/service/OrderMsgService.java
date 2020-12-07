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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 更新订单状态
     *
     * @param order
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeOrderStatus(OrderStatus order) {
        TKdsOrder tOrder = kdsOrderDao.query(order.getOrderNo());
        if (tOrder == null) {
            log.info("[changeOrderStatus] not find order :{}", order.getOrderNo());
            return;
        }
        updateOrder(order.getUuid(), tOrder, ActionEnum.getLogActionByCallAction(order.getLogAction()));
    }

    private void updateOrder(String uuid, TKdsOrder tOrder, String newLogAction) {
        String oldLogAction = tOrder.getLogAction();
        // 检查传入的新状态是否有效
        List<String> validLogActions = ActionEnum.VALID_LOG_ACTION_MAP.get(oldLogAction);
        if (validLogActions != null && validLogActions.contains(newLogAction)) {
            KdsOrder kdsOrder = JSON.parseObject(tOrder.getData(), KdsOrder.class);
            if (ActionEnum.ORDER_COMPLETE.getLogAction().equals(newLogAction) || ActionEnum.ORDER_DELIVERYING.getLogAction().equals(newLogAction)) {
                tOrder.setCompleted(true);
            } else if (ActionEnum.ORDER_MAKE.getLogAction().equals(newLogAction)) {
                tOrder.setMakeTime(new Date());
            } else if (ActionEnum.ORDER_TAKING.getLogAction().equals(newLogAction)) {
                tOrder.setCallTime(new Date());
            }
            kdsOrder.setCallAction(ActionEnum.getCallActionByLogAction(newLogAction));
            tOrder.setData(JSON.toJSONString(kdsOrder));
            tOrder.setLogAction(newLogAction);
            kdsOrderDao.updateOrder(tOrder);
            createMsg(uuid, tOrder);
        } else {
            log.info("[updateOrder] {} check logAction is valid, oldAction:{}, newAction:{}", tOrder.getNo(), oldLogAction, newLogAction);
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

    public void batchCompleteByTypes(int hqId, int branchId, String types) {
        types = "'" + types.replaceAll(",", "','") + "'";
        List<TKdsOrder> orders = kdsOrderDao.queryAllUncompleted(hqId, branchId, types);
        for (TKdsOrder order : orders) {
            try {
                updateOrder(null, order, ActionEnum.ORDER_COMPLETE.getLogAction());
            } catch (Exception e) {
                e.printStackTrace();
                log.info("[batchCompleteByTypes] complete no:{} fail, error:{} ", order.getNo(), e.getMessage());
            }
        }
    }
}
