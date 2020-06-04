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

    private String taskName;

    public QueryMsgTask(KdsMsgDao kdsMsgDao, int number) {
        this.kdsMsgDao = kdsMsgDao;
        this.taskName = "QueryMsgTask-" + number;
    }

    @Override
    public void run() {
        log.info("[{}] start run", taskName);
        List<KdsMessage> messages;
        List<QueryMsg> queryMsgList;
        QueryMsg queryMsg;
        QueryUnit queryUNIT;
        Date startTime;
        while (true) {
            try {
                queryUNIT = TaskManager.fetchWorkQueue().poll(5, TimeUnit.SECONDS);
                if (queryUNIT == null) {
                    Thread.sleep(100);
                    continue;
                }
                log.info("[{}] {} {} query start", taskName, queryUNIT.getBranchId(), queryUNIT.getUuid());
                startTime = DatetimeUtil.dayBegin(new Date());
                messages = kdsMsgDao.queryUnPushed(queryUNIT.getBranchId(), queryUNIT.getUuid(), startTime, -1);
                queryMsgList = new ArrayList<>(messages.size());
                for (KdsMessage msg : messages) {
                    queryMsg = new QueryMsg();
                    queryMsg.setMsgId(msg.getMessageId());
                    queryMsg.setOrderNo(msg.getOrderNo());
                    queryMsg.setLogAction(msg.getLogAction());
                    queryMsg.setOrder(msg.getData());
                    queryMsgList.add(queryMsg);
                }
                TaskManager.putQueryData(queryUNIT.getUuid(), queryMsgList);
                log.info("[{}] {} {} query end", taskName, queryUNIT.getBranchId(), queryUNIT.getUuid());
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("[{}] happen error:{}", taskName, e.getMessage());
            }
        }
    }
}
