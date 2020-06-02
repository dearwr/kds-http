package com.hchc.kdshttp.service;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.constant.ActionEnum;
import com.hchc.kdshttp.constant.OrderType;
import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.dao.KdsOrderDao;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.entity.KdsOrder;
import com.hchc.kdshttp.mode.kds.KdsOrderItem;
import com.hchc.kdshttp.mode.request.OrderStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Service
public class OrderMsgService {

    private static final Logger logger = LogManager.getLogger(OrderMsgService.class.getName());

    @Autowired
    private KdsMsgService kdsMsgService;

    @Autowired
    private KdsMsgDao kdsMsgDao;

    @Autowired
    private KdsOrderDao kdsOrderDao;

    /**
     * 消息确认同步订单状态
     *
     * @param msgIds
     * @return
     */
    public boolean confirm(List<String> msgIds) {
        return kdsMsgDao.updatePushed(msgIds);
    }

    /**
     * 根据订单号更新订单状态并生成消息
     *
     * @param order
     * @return
     */
    public void changeOrderStatus(OrderStatus order) {
        KdsOrder tOrder = kdsOrderDao.query(order.getOrderNo());
        if (tOrder == null) {
            logger.info("[getMsgByOrderNo] not find order :{}", order.getOrderNo());
            return;
        }
        updateOrder(order.getUuid(), tOrder, order.getLogAction());
    }

    private void updateOrder(String uuid, KdsOrder tOrder, String newLogAction) {
        String oldLogAction = tOrder.getLogAction();
        // 检查传入的新状态是否有效
        List<String> validLogActions = ActionEnum.VALID_LOG_ACTION_MAP.get(oldLogAction);
        if (validLogActions != null && validLogActions.contains(newLogAction)) {
            com.hchc.kdshttp.mode.kds.KdsOrder kdsOrder = JSON.parseObject(tOrder.getData(), com.hchc.kdshttp.mode.kds.KdsOrder.class);
            checkRefundOrCompleted(tOrder, kdsOrder, newLogAction);
            kdsOrder.setCallAction(ActionEnum.getCallActionByLogAction(newLogAction));
            tOrder.setData(JSON.toJSONString(kdsOrder));
            tOrder.setLogAction(newLogAction);
            tOrder.setUpdateTime(new Date());
            kdsOrderDao.updateOrder(tOrder);
            createMsg(uuid, tOrder);
        } else {
            logger.info("[updateOrder] {} check logAction is valid, oldAction:{}, newAction:{}", tOrder.getNo(), oldLogAction, newLogAction);
            return;
        }
    }

    private void createMsg(String uuid, KdsOrder tOrder) {
        if (ActionEnum.INVALID_LOG_ACTION_MAP.get(tOrder.getLogAction()) != null) {
            List<String> invalidLogActions = new ArrayList<>(ActionEnum.INVALID_LOG_ACTION_MAP.get(tOrder.getLogAction()));
            kdsMsgDao.updateInvalidMsg(tOrder.getNo(), invalidLogActions);
        }
        List<KdsMessage> messages = kdsMsgService.createMsgByOrder(tOrder, null, uuid);
        kdsMsgDao.batchAdd(messages);
    }

    /**
     * 检查是否为退单或是订单完成
     *
     * @param tOrder
     * @param kdsOrder
     * @param newLogAction
     */
    private void checkRefundOrCompleted(KdsOrder tOrder, com.hchc.kdshttp.mode.kds.KdsOrder kdsOrder, String newLogAction) {
        // 判断newLogAction是否为退单
        if (ActionEnum.ORDER_REFUND.getLogAction().equals(newLogAction)) {
            tOrder.setCompleted(true);
            kdsOrder.setParentOrderNo(kdsOrder.getOrderNo());
            for (KdsOrderItem item : kdsOrder.getItems()) {
                item.setPurpose("REFUNDED");
                item.setRefunded((int) item.getCount());
            }
        }
        // 判断newLogAction是否为订单完成
        if (ActionEnum.ORDER_COMPLETE.getLogAction().equals(newLogAction)) {
            tOrder.setCompleted(true);
        }
    }

}
