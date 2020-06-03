package com.hchc.kdshttp.task;

import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.entity.KdsMessage;
import com.hchc.kdshttp.mode.request.QueryParam;
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
        QueryParam queryParam;
        Date startTime;
        while (true) {
            try {
                queryParam = Task.WORKER_QUEUE.poll(5, TimeUnit.SECONDS);
                if (queryParam == null) {
                    Thread.sleep(100);
                    continue;
                }
                log.info("[{}] {} {} query start", taskName, queryParam.getBranchId(), queryParam.getUuid());
                startTime = DatetimeUtil.dayBegin(new Date());
                messages = kdsMsgDao.queryUnPushed(queryParam.getBranchId(), queryParam.getUuid(), startTime, -1);
                queryMsgList = new ArrayList<>(messages.size());
                for (KdsMessage msg : messages) {
                    queryMsg = new QueryMsg();
                    queryMsg.setMsgId(msg.getMessageId());
                    queryMsg.setOrderNo(msg.getOrderNo());
                    queryMsg.setLogAction(msg.getLogAction());
                    queryMsg.setOrder(msg.getData());
                    queryMsgList.add(queryMsg);
                }
                Task.QUERY_DATA.putIfAbsent(queryParam.getUuid(), queryMsgList);
                log.info("[{}] {} {} query end", taskName, queryParam.getBranchId(), queryParam.getUuid());
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("[{}] happen error:{}", taskName, e.getMessage());
            }
        }
    }
}
