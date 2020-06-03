package com.hchc.kdshttp.service;

import com.hchc.kdshttp.dao.BranchKdsDao;
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

    /**
     * 连接加载消息
     *
     * @param kdsInfo
     * @return
     */
    public void bindKds(KdsInfo kdsInfo) {
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
            boolean versionChanged = judgeVersionChanged(oldKds, kdsInfo);
            if (newHqId != oldKds.getHqId() || newBranchId != oldKds.getBranchId()) {
                log.info("[bindKds] kds change hqId or branch, uuid:{}", uuid);
                oldKds.setHqId(newHqId);
                oldKds.setBranchId(newBranchId);
                branchKdsDao.update(oldKds);
                Date end = new Date();
                Date start = DatetimeUtil.dayBegin(end);
                kdsMsgDao.updateInvalidMsg(newBranchId, uuid, start, end);
                createBranchMsg(kdsInfo);
            } else {
                if (versionChanged) {
                    branchKdsDao.update(oldKds);
                }
            }
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

    private void createBranchMsg(KdsInfo kdsInfo) {
        Date end = new Date();
        Date start = DatetimeUtil.dayBegin(end);
        List<TKdsOrder> orders = kdsOrderDao.queryUncompleted(kdsInfo.getHqId(), kdsInfo.getBranchId(), start, end);
        List<KdsMessage> messages = new ArrayList<>();
        for (TKdsOrder order : orders) {
            messages.addAll(kdsMsgService.createMsgByOrder(order, Collections.singletonList(kdsInfo.getDeviceUUID()), null));
        }
        kdsMsgDao.batchAdd(messages);
    }

    public List<String> queryUUIDs(int hqId, int branchId) {
        return branchKdsDao.queryUUIDs(hqId, branchId);
    }

}
