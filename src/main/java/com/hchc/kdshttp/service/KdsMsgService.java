package com.hchc.kdshttp.service;

import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.dao.KdsOrderDao;
import com.hchc.kdshttp.entity.TBranchKds;
import com.hchc.kdshttp.entity.TKdsMessage;
import com.hchc.kdshttp.entity.TKdsOrder;
import com.hchc.kdshttp.mode.KdsInfo;
import com.hchc.kdshttp.util.DatetimeUtil;
import com.hchc.kdshttp.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
public class KdsMsgService {

    private static final Logger logger = LogManager.getLogger(KdsMsgService.class.getName());

    @Autowired
    private KdsMsgDao kdsMsgDao;

    @Autowired
    private KdsOrderDao kdsOrderDao;

    @Autowired
    private BranchKdsService branchKdsService;

    /**
     * 根据订单生成消息
     *
     * @param tOrder        订单
     * @param uuidList      需要生成消息的uuid集合
     * @param completedUUID 消息设置为推送完成的uuid
     * @return
     */
    public List<TKdsMessage> createMsgByOrder(TKdsOrder tOrder, List<String> uuidList, String completedUUID) {
        if (CollectionUtils.isEmpty(uuidList)) {
            uuidList = branchKdsService.queryUUIDs(tOrder.getHqId(), tOrder.getBranchId());
            if (CollectionUtils.isEmpty(uuidList)) {
                logger.info("[createMsgByOrder] not find uuid, no: {} , branchId:{}", tOrder.getNo(), tOrder.getBranchId());
                return Collections.emptyList();
            }
        }
        List<TKdsMessage> messages = new ArrayList<>();
        TKdsMessage message;
        for (String uuid : uuidList) {
            message = new TKdsMessage();
            message.setMessageId(StringUtil.generateMessageId("kds"));
            message.setBranchId(tOrder.getBranchId());
            message.setData(tOrder.getData());
            message.setLogAction(tOrder.getLogAction());
            message.setUuid(uuid);
            message.setOrderNo(tOrder.getNo());
            message.setType(tOrder.getType());
            if (uuid.equals(completedUUID)) {
                message.setPushed(true);
                message.setPushedTime(new Date());
            }
            messages.add(message);
        }
        return messages;
    }

    /**
     * 连接加载消息
     *
     * @param kdsInfo
     * @return
     */
    public List<TKdsMessage> loadMsgForConnect(KdsInfo kdsInfo) {
        String uuid = kdsInfo.getDeviceUUID();
        TBranchKds oldKds = branchKdsService.queryByUUID(uuid);
        if (oldKds == null) {
            logger.info("[loadMsgForConnect] create kds to db, uuid:{}", uuid);
            TBranchKds kds = new TBranchKds();
            kds.setHqId(kdsInfo.getHqId());
            kds.setBranchId(kdsInfo.getBranchId());
            kds.setUuid(kdsInfo.getDeviceUUID());
            kds.setVersion(kdsInfo.getVersionCode());
            branchKdsService.saveKds(kds);
            return queryBranchMsg(kdsInfo);
        } else {
            int newHqId = kdsInfo.getHqId();
            int newBranchId = kdsInfo.getBranchId();
            oldKds.setOpen(true);
            boolean versionChanged = judgeVersionChanged(oldKds, kdsInfo);
            if (newHqId != oldKds.getHqId() || newBranchId != oldKds.getBranchId()) {
                logger.info("[loadMsgForConnect] kds change hqId or branch, uuid:{}", uuid);
                oldKds.setHqId(newHqId);
                oldKds.setBranchId(newBranchId);
                branchKdsService.update(oldKds);
                Date end = new Date();
                Date start = DatetimeUtil.dayBegin(end);
                kdsMsgDao.updateInvalidMsg(newBranchId, uuid, start, end);
                return queryBranchMsg(kdsInfo);
            } else {
                if (versionChanged) {
                    branchKdsService.update(oldKds);
                }
                return loadUnPushedMsg(oldKds.getUuid(), null, null, -1);
            }
        }
    }

    /**
     * 判断版本号是否改变
     *
     * @param oldKds
     * @param kdsInfo
     */
    private boolean judgeVersionChanged(TBranchKds oldKds, KdsInfo kdsInfo) {
        String newVersion = kdsInfo.getVersionCode();
        if (!StringUtils.isEmpty(newVersion) && !newVersion.equals(oldKds.getVersion())) {
            logger.info("[judgeVersionChanged] kds change version {} to {}, uuid:{}", oldKds.getVersion(), newVersion, oldKds.getUuid());
            oldKds.setVersion(newVersion);
            return true;
        }
        return false;
    }

    private List<TKdsMessage> queryBranchMsg(KdsInfo kdsInfo) {
        Date end = new Date();
        Date start = DatetimeUtil.dayBegin(end);
        List<TKdsOrder> orders = kdsOrderDao.queryUncompleted(kdsInfo.getHqId(), kdsInfo.getBranchId(), start, end);
        List<TKdsMessage> messages = new ArrayList<>();
        for (TKdsOrder order : orders) {
            messages.addAll(createMsgByOrder(order, Collections.singletonList(kdsInfo.getDeviceUUID()), null));
        }
        kdsMsgDao.batchAdd(messages);
        return messages;
    }

    /**
     * 加载消息
     * 入参若没有起始时间，默认传当天所有的消息
     *
     * @param uuid
     * @param size
     * @return
     */
    public List<TKdsMessage> loadUnPushedMsg(String uuid, Date startTime, Date endTime, int size) {
        if (startTime == null) {
            endTime = new Date();
            startTime = DatetimeUtil.dayBegin(endTime);
        }
        return kdsMsgDao.queryUnPushed(uuid, startTime, endTime, size);
    }

    /**
     * 查询订单最新状态的消息
     *
     * @param uuid
     * @param orderNo
     * @return
     */
    public TKdsMessage queryOrderNewStatusMsg(String uuid, String orderNo) {
        List<TKdsMessage> messages = kdsMsgDao.queryOrderMsg(uuid, orderNo);
        if (CollectionUtils.isEmpty(messages)) {
            logger.info("[queryOrderNewStatusMsg] not find msg, uuid:{}, no:{}", uuid, orderNo);
            return null;
        }
        List<String> actions = messages.stream().map(TKdsMessage::getLogAction).collect(Collectors.toList());
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

    private TKdsMessage getMsgByAction(String action, List<TKdsMessage> messages) {
        for (TKdsMessage msg : messages) {
            if (action.equals(msg.getLogAction())) {
                return msg;
            }
        }
        return null;
    }

}
