package com.hchc.kdshttp.service;

import com.hchc.kdshttp.dao.BranchKdsDao;
import com.hchc.kdshttp.entity.BranchKds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
/**
 * @author wangrong
 * @date 2020-06-02
 */
@Service
public class BranchKdsService {

    @Autowired
    private BranchKdsDao branchKdsDao;

    public boolean saveKds(BranchKds kds) {
        return branchKdsDao.add(kds);
    }

    public BranchKds queryByUUID(String uuid) {
        return branchKdsDao.query(uuid);
    }


    public void update(BranchKds oldKds) {
        branchKdsDao.update(oldKds);
    }

    public List<String> queryUUIDs(int hqId, int branchId) {
        return branchKdsDao.queryUUIDs(hqId, branchId);
    }

    public void updateHeartTime(Date heartTime, int hqId, int branchId, String uuid) {
        branchKdsDao.updateHeartTime(heartTime, hqId, branchId, uuid);
    }
}
