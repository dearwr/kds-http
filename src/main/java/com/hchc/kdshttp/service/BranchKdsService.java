package com.hchc.kdshttp.service;

import com.hchc.kdshttp.dao.BranchKdsDao;
import com.hchc.kdshttp.dao.HqFeatureDao;
import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.dao.KdsOrderDao;
import com.hchc.kdshttp.entity.BranchKds;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.entity.TKdsOrder;
import com.hchc.kdshttp.mode.HqFeature;
import com.hchc.kdshttp.mode.request.KdsInfo;
import com.hchc.kdshttp.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

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
        List<HqFeature> hqFeatureList = hqFeatureDao.queryWeChatQueueEnable(kdsInfo.getHqId());
        if (CollectionUtils.isEmpty(hqFeatureList) || "INVALID".equals(hqFeatureList.get(0).getStatus())) {
            throw new Exception("品牌尚未开启外网版kds功能");
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
            oldKds.setOpen(true);
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
        String types = "'STORE','MALL','ONLINE','DELIVERY'";
        List<TKdsOrder> orders = kdsOrderDao.queryUncompleted(kdsInfo.getHqId(), kdsInfo.getBranchId(), start, types);
        List<KdsMessage> messages = new ArrayList<>();
        for (TKdsOrder order : orders) {
            messages.addAll(kdsMsgService.createUnCompleteOrderMsg(order, Collections.singletonList(kdsInfo.getDeviceUUID()), null));
        }
        kdsMsgDao.batchAdd(messages);
    }

    public List<String> queryUUIDs(int hqId, int branchId) {
        return branchKdsDao.queryUUIDs(hqId, branchId);
    }

    /**
     * 解绑 kds
     *
     * @param branchId
     * @param uuid
     */
    @Transactional(rollbackFor = Exception.class)
    public void unBindKds(int branchId, String uuid) {
        branchKdsDao.unBind(branchId, uuid);
        if (branchKdsDao.queryBindKdsCount(branchId) == 0) {
            branchKdsDao.closeWeChatQueueEnable(branchId);
//            Date end = new Date();
//            Date start = DatetimeUtil.dayBegin(end);
//            kdsOrderDao.completeOrders(branchId, start, end);
        }
    }

    public boolean queryWeChatQueueEnable(int branchId) {
        return branchKdsDao.queryWeChatQueueEnable(branchId);
    }
}
