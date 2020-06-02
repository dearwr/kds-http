package com.hchc.kdshttp.service;

import com.alibaba.fastjson.JSON;
import com.hchc.kdshttp.constant.ActionEnum;
import com.hchc.kdshttp.constant.OrderType;
import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.dao.KdsOrderDao;
import com.hchc.kdshttp.entity.TKdsMessage;
import com.hchc.kdshttp.entity.TKdsOrder;
import com.hchc.kdshttp.mode.KdsOrder;
import com.hchc.kdshttp.mode.KdsOrderItem;
import com.hchc.kdshttp.util.DatetimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Service
public class KdsOrderMsgService {

    private static final Logger logger = LogManager.getLogger(KdsOrderMsgService.class.getName());

    @Autowired
    private KdsMsgService kdsMsgService;

    @Autowired
    private KdsMsgDao kdsMsgDao;

    @Autowired
    private KdsOrderDao kdsOrderDao;

    /**
     * 消息确认同步订单状态
     *
     * @param messageId
     * @return
     */
    public boolean confirm(String messageId) {
        return kdsMsgDao.updatePushed(messageId);
    }

    /**
     * 根据订单号更新订单状态并生成消息
     *
     * @param uuid
     * @param orderNo
     * @param newLogAction
     * @return
     */
    public List<TKdsMessage> getMsgByOrderNo(String uuid, String orderNo, String newLogAction) {
        TKdsOrder tOrder = kdsOrderDao.query(orderNo);
        if (tOrder == null) {
            logger.info("[getMsgByOrderNo] not find order :{}", orderNo);
            return Collections.emptyList();
        }
        return updateOrder(uuid, tOrder, newLogAction);
    }

    private List<TKdsMessage> updateOrder(String uuid, TKdsOrder tOrder, String newLogAction) {
        String oldLogAction = tOrder.getLogAction();
        // 检查传入的新状态是否有效
        List<String> validLogActions = ActionEnum.VALID_LOG_ACTION_MAP.get(oldLogAction);
        if (validLogActions != null && validLogActions.contains(newLogAction)) {
            KdsOrder kdsOrder = JSON.parseObject(tOrder.getData(), KdsOrder.class);
            checkRefundOrCompleted(tOrder, kdsOrder, newLogAction);
            kdsOrder.setCallAction(ActionEnum.getCallActionByLogAction(newLogAction));
            tOrder.setData(JSON.toJSONString(kdsOrder));
            tOrder.setLogAction(newLogAction);
            tOrder.setUpdateTime(new Date());
            return updateOrderAndGetMsg(uuid, tOrder);
        } else {
            logger.info("[updateOrder] {} check logAction is valid, oldAction:{}, newAction:{}", tOrder.getNo(), oldLogAction, newLogAction);
            return Collections.emptyList();
        }
    }

    private List<TKdsMessage> updateOrderAndGetMsg(String uuid, TKdsOrder tOrder) {
        kdsOrderDao.updateOrder(tOrder);
        return createAndGetMsg(uuid, tOrder);
    }

    private List<TKdsMessage> createAndGetMsg(String uuid, TKdsOrder tOrder) {
        if (ActionEnum.INVALID_LOG_ACTION_MAP.get(tOrder.getLogAction()) != null) {
            List<String> invalidLogActions = new ArrayList<>(ActionEnum.INVALID_LOG_ACTION_MAP.get(tOrder.getLogAction()));
            if (OrderType.STORE.equals(tOrder.getType())) {
                invalidLogActions.remove(ActionEnum.ORDER_MAKE.getLogAction());
            }
            if (!CollectionUtils.isEmpty(invalidLogActions)) {
                kdsMsgDao.updateInvalidMsg(tOrder.getNo(), invalidLogActions);
            }
        }
        List<TKdsMessage> messages = kdsMsgService.createMsgByOrder(tOrder, null, uuid);
        kdsMsgDao.batchAdd(messages);
        return messages;
    }

    /**
     * 检查是否为退单或是订单完成
     *
     * @param tOrder
     * @param kdsOrder
     * @param newLogAction
     */
    private void checkRefundOrCompleted(TKdsOrder tOrder, KdsOrder kdsOrder, String newLogAction) {
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

    /**
     * 清除数据库表历史数据
     *
     * @param dayBefore
     */
    public void cleanDayBeforeOrderAndMsg(int dayBefore) {
        Date endTime = DatetimeUtil.addDay(new Date(), -dayBefore);
        int count = kdsOrderDao.deleteBeforeTime(endTime);
        logger.info("[cleanDayBeforeOrderAndMsg] clean order count:{}", count);
        count = kdsMsgDao.deleteBeforeTime(endTime);
        logger.info("[cleanDayBeforeOrderAndMsg] clean msg count:{}", count);
    }
}
