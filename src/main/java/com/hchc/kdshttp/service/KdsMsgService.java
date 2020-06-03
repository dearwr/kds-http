package com.hchc.kdshttp.service;

import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.entity.TKdsOrder;
import com.hchc.kdshttp.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hchc.kdshttp.constant.ActionEnum.*;


/**
 * @author wangrong
 * @date 2020-06-02
 */
@Service
@Slf4j
public class KdsMsgService {

    @Autowired
    private KdsMsgDao kdsMsgDao;

    @Autowired
    private BranchKdsService branchKdsService;

    /**
     * 确认消息接收
     * @param msgIds
     * @return
     */
    public boolean confirmMsg(List<String> msgIds) {
        return kdsMsgDao.updatePushed(msgIds);
    }

    /**
     * 根据订单生成消息
     *
     * @param tOrder        订单
     * @param uuidList      需要生成消息的uuid
     * @param completedUuid 消息设置为完成的uuid
     * @return
     */
    public List<KdsMessage> createMsgByOrder(TKdsOrder tOrder, List<String> uuidList, String completedUuid) {
        if (CollectionUtils.isEmpty(uuidList)) {
            uuidList = branchKdsService.queryUUIDs(tOrder.getHqId(), tOrder.getBranchId());
            if (CollectionUtils.isEmpty(uuidList)) {
                log.info("[createMsgByOrder] not find uuid, no: {} , branchId:{}", tOrder.getNo(), tOrder.getBranchId());
                return Collections.emptyList();
            }
        }
        List<KdsMessage> messages = new ArrayList<>();
        KdsMessage message;
        for (String uuid : uuidList) {
            message = new KdsMessage();
            message.setMessageId(StringUtil.generateMessageId("kds"));
            message.setBranchId(tOrder.getBranchId());
            message.setData(tOrder.getData());
            message.setLogAction(tOrder.getLogAction());
            message.setUuid(uuid);
            message.setOrderNo(tOrder.getNo());
            message.setType(tOrder.getType());
            if (uuid.equals(completedUuid)) {
                message.setPushed(true);
                message.setPushedTime(new Date());
            }
            messages.add(message);
        }
        return messages;
    }

    /**
     * 查询订单最新状态的消息
     *
     * @param uuid
     * @param orderNo
     * @return
     */
    public KdsMessage queryOrderNewStatusMsg(String uuid, String orderNo) {
        List<KdsMessage> messages = kdsMsgDao.queryOrderMsg(uuid, orderNo);
        if (CollectionUtils.isEmpty(messages)) {
            log.info("[queryOrderNewStatusMsg] not find msg, uuid:{}, no:{}", uuid, orderNo);
            return null;
        }
        List<String> actions = messages.stream().map(KdsMessage::getLogAction).collect(Collectors.toList());
        if (actions.contains(ORDER_REFUND.getLogAction())) {
            return getMsgByAction(ORDER_REFUND.getLogAction(), messages);
        } else if (actions.contains(ORDER_COMPLETE.getLogAction())) {
            return getMsgByAction(ORDER_COMPLETE.getLogAction(), messages);
        } else if (actions.contains(ORDER_DELIVERYING.getLogAction())) {
            return getMsgByAction(ORDER_DELIVERYING.getLogAction(), messages);
        } else if (actions.contains(ORDER_TAKING.getLogAction())) {
            return getMsgByAction(ORDER_TAKING.getLogAction(), messages);
        } else if (actions.contains(ORDER_MAKE.getLogAction())) {
            return getMsgByAction(ORDER_MAKE.getLogAction(), messages);
        } else if (actions.contains(ORDER_PRE.getLogAction())) {
            return getMsgByAction(ORDER_PRE.getLogAction(), messages);
        } else {
            return getMsgByAction(ORDER_NEW.getLogAction(), messages);
        }
    }

    private KdsMessage getMsgByAction(String action, List<KdsMessage> messages) {
        for (KdsMessage msg : messages) {
            if (action.equals(msg.getLogAction())) {
                return msg;
            }
        }
        return null;
    }

}
