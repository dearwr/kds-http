package com.hchc.kdshttp.task;

import com.hchc.kdshttp.dao.BranchKdsDao;
import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.mode.request.QueryUnit;
import com.hchc.kdshttp.mode.response.QueryMsg;
import com.hchc.kdshttp.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wangrong
 * @date 2020-06-03
 */
@Slf4j
public class QueryMsgTask implements Runnable {

    private KdsMsgDao kdsMsgDao;
    private BranchKdsDao branchKdsDao;

    public QueryMsgTask(KdsMsgDao kdsMsgDao, BranchKdsDao branchKdsDao) {
        this.kdsMsgDao = kdsMsgDao;
        this.branchKdsDao = branchKdsDao;
    }

    @Override
    public void run() {
        log.info("[QueryMsgTask] start run");
        List<KdsMessage> messages;
        List<QueryMsg> queryMsgList;
        QueryMsg queryMsg;
        QueryUnit queryUnit;
        Date startTime;
        while (true) {
            try {
                queryUnit = TaskManager.fetchWorkQueue().poll(1, TimeUnit.SECONDS);
                if (queryUnit == null) {
                    Thread.sleep(50);
                    continue;
                }
                log.info("[QueryMsgTask] {} {} start query", queryUnit.getBranchId(), queryUnit.getUuid());
                startTime = DatetimeUtil.dayBegin(new Date());
                messages = kdsMsgDao.queryUnPushed(queryUnit.getUuid(), startTime, 25);
                queryMsgList = new ArrayList<>(messages.size());
                for (KdsMessage msg : messages) {
                    queryMsg = new QueryMsg();
                    queryMsg.setMsgId(msg.getMessageId());
                    queryMsg.setOrderNo(msg.getOrderNo());
                    queryMsg.setLogAction(msg.getLogAction());
                    queryMsg.setOrder(msg.getData());
                    queryMsgList.add(queryMsg);
                }
                TaskManager.setQueryData(queryUnit.getUuid(), queryMsgList);
                TaskManager.removeWait(queryUnit.getUuid());
                branchKdsDao.updateHeartTime(new Date(), queryUnit.getUuid());
                log.info("[QueryMsgTask] {} {} end query", queryUnit.getBranchId(), queryUnit.getUuid());
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("[QueryMsgTask]happen error:{}", e.getMessage());
            }
        }
    }
}
