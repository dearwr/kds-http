package com.hchc.kdshttp.service;

import com.hchc.kdshttp.dao.BranchKdsDao;
import com.hchc.kdshttp.dao.HqFeatureDao;
import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.dao.KdsOrderDao;
import com.hchc.kdshttp.entity.BranchKds;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.entity.TKdsOrder;
import com.hchc.kdshttp.mode.request.KdsInfo;
import com.hchc.kdshttp.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
public class BranchKdsService {

    @Autowired
    private BranchKdsDao branchKdsDao;

    @Autowired
    private KdsOrderDao kdsOrderDao;

    @Autowired
    private KdsMsgDao kdsMsgDao;

    @Autowired
    private KdsMsgService kdsMsgService;

    @Autowired
    private HqFeatureDao hqFeatureDao;

    /**
     * 绑定kds
     *
     * @param kdsInfo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindKds(KdsInfo kdsInfo) throws Exception {
        if (!hqFeatureDao.queryWeChatQueueEnable(kdsInfo.getHqId())) {
            throw new Exception("未开通kds接单功能");
        }
        String uuid = kdsInfo.getDeviceUUID();
        BranchKds oldKds = branchKdsDao.query(uuid);
        if (oldKds == null) {
            log.info("[bindKds] create kds to db, uuid:{}", uuid);
            BranchKds kds = new BranchKds();
            kds.setHqId(kdsInfo.getHqId());
            kds.setBranchId(kdsInfo.getBranchId());
            kds.setUuid(kdsInfo.getDeviceUUID());
            kds.setVersion(kdsInfo.getVersionCode());
            branchKdsDao.add(kds);
            createBranchMsg(kdsInfo);
        } else {
            int newHqId = kdsInfo.getHqId();
            int newBranchId = kdsInfo.getBranchId();
            oldKds.setOpen(true);
            judgeVersionChanged(oldKds, kdsInfo);
            if (newHqId != oldKds.getHqId() || newBranchId != oldKds.getBranchId()) {
                log.info("[bindKds] kds change hqId or branch, uuid:{}", uuid);
                oldKds.setHqId(newHqId);
                oldKds.setBranchId(newBranchId);
                Date end = new Date();
                Date start = DatetimeUtil.dayBegin(end);
                kdsMsgDao.updateInvalidMsg(uuid, start, end);
                createBranchMsg(kdsInfo);
            }
            branchKdsDao.update(oldKds);
        }
    }

    /**
     * 判断版本号是否改变
     *
     * @param oldKds
     * @param kdsInfo
     */
    private boolean judgeVersionChanged(BranchKds oldKds, KdsInfo kdsInfo) {
        String newVersion = kdsInfo.getVersionCode();
        if (!StringUtils.isEmpty(newVersion) && !newVersion.equals(oldKds.getVersion())) {
            log.info("[judgeVersionChanged] kds change version {} to {}, uuid:{}", oldKds.getVersion(), newVersion, oldKds.getUuid());
            oldKds.setVersion(newVersion);
            return true;
        }
        return false;
    }

    /**
     * 门店未完成的订单创建该uuid设备消息
     *
     * @param kdsInfo
     */
    private void createBranchMsg(KdsInfo kdsInfo) {
        Date start = DatetimeUtil.dayBegin(new Date());
        List<TKdsOrder> orders = kdsOrderDao.queryUncompleted(kdsInfo.getHqId(), kdsInfo.getBranchId(), start);
        List<KdsMessage> messages = new ArrayList<>();
        for (TKdsOrder order : orders) {
            messages.addAll(kdsMsgService.createMsgByOrder(order, Collections.singletonList(kdsInfo.getDeviceUUID()), null));
        }
        kdsMsgDao.batchAdd(messages);
    }

    public List<String> queryUUIDs(int hqId, int branchId) {
        return branchKdsDao.queryUUIDs(hqId, branchId);
    }

    public void unBindKds(int branchId, String uuid) {
        branchKdsDao.unBind(branchId, uuid);
    }
}
