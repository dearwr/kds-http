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
     *
     * @param msgIds
     * @return
     */
    public boolean confirmMsg(List<String> msgIds) {
        return kdsMsgDao.confirmMsg(msgIds);
    }

    /**
     * 根据门店未完成的订单生成消息
     *
     * @param tOrder        订单
     * @param uuidList      需要生成消息的uuid
     * @return
     */
    public List<KdsMessage> createUnCompleteOrderMsg(TKdsOrder tOrder, List<String> uuidList) {
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
            if (kdsMsgDao.queryExist(tOrder.getNo(), uuid, tOrder.getLogAction())) {
                continue;
            }
            message = new KdsMessage();
            message.setMessageId(StringUtil.generateMessageId("kds"));
            message.setBranchId(tOrder.getBranchId());
            message.setData(tOrder.getData());
            message.setLogAction(tOrder.getLogAction());
            message.setUuid(uuid);
            message.setOrderNo(tOrder.getNo());
            message.setType(tOrder.getType());
            messages.add(message);
        }
        return messages;
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
    public KdsMessage queryOrderNewMsg(String uuid, String orderNo) throws Exception {
        List<KdsMessage> messages = kdsMsgDao.queryNewOrderMsg(uuid, orderNo);
        if (CollectionUtils.isEmpty(messages)) {
            log.info("[queryOrderNewMsg] not find order msg, uuid:{}, no:{}", uuid, orderNo);
            throw new Exception("not find order msg");
        }
        return messages.get(0);
    }

}
