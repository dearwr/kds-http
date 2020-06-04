package com.hchc.kdshttp.task;

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

    public QueryMsgTask(KdsMsgDao kdsMsgDao) {
        this.kdsMsgDao = kdsMsgDao;
    }

    @Override
    public void run() {
        List<KdsMessage> messages;
        List<QueryMsg> queryMsgList;
        QueryMsg queryMsg;
        QueryUnit queryUnit;
        Date startTime;
        while (true) {
            try {
                queryUnit = TaskManager.fetchWorkQueue().poll(5, TimeUnit.SECONDS);
                if (queryUnit == null) {
                    Thread.sleep(100);
                    continue;
                }
                log.info("query {} {} start", queryUnit.getBranchId(), queryUnit.getUuid());
                startTime = DatetimeUtil.dayBegin(new Date());
                messages = kdsMsgDao.queryUnPushed(queryUnit.getBranchId(), queryUnit.getUuid(), startTime, 30);
                queryMsgList = new ArrayList<>(messages.size());
                for (KdsMessage msg : messages) {
                    queryMsg = new QueryMsg();
                    queryMsg.setMsgId(msg.getMessageId());
                    queryMsg.setOrderNo(msg.getOrderNo());
                    queryMsg.setLogAction(msg.getLogAction());
                    queryMsg.setOrder(msg.getData());
                    queryMsgList.add(queryMsg);
                }
                TaskManager.putQueryData(queryUnit.getUuid(), queryMsgList);
                TaskManager.removeWaitUnit(queryUnit.getUuid());
                log.info("query {} {} end", queryUnit.getBranchId(), queryUnit.getUuid());
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("happen error:{}", e.getMessage());
            }
        }
    }
}
