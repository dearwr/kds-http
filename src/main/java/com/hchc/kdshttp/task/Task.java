package com.hchc.kdshttp.task;

import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.mode.request.QueryParam;
import com.hchc.kdshttp.mode.response.QueryMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author wangrong
 * @date 2020-06-03
 */
@Service
@Slf4j
public class Task {

    @Autowired
    private KdsMsgDao kdsMsgDao;

    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    public static final Map<String, List<QueryMsg>> QUERY_DATA = new ConcurrentHashMap<>();
    public static final BlockingQueue<QueryParam> WORKER_QUEUE = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 10; i++) {
            EXECUTOR.submit(new QueryMsgTask(kdsMsgDao, i));
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Task shutdown");
    }

}
