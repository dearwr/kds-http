package com.hchc.kdshttp.task;

import com.hchc.kdshttp.dao.BranchKdsDao;
import com.hchc.kdshttp.dao.KdsMsgDao;
import com.hchc.kdshttp.mode.request.QueryUnit;
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
public class TaskManager {

    @Autowired
    private KdsMsgDao kdsMsgDao;

    @Autowired
    private BranchKdsDao branchKdsDao;

    private static final int QUERY_TASK_COUNT = 10;
    private static final ExecutorService QUERY_THREAD_POOL = new ThreadPoolExecutor(10, 10,
            60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new CustomerThreadFactory("query"));

    private static final Map<String, List<QueryMsg>> QUERY_DATA = new ConcurrentHashMap<>();
    private static final Map<String, QueryUnit> WAIT_QUERY = new ConcurrentHashMap<>();
    private static final BlockingQueue<QueryUnit> QUERY_QUEUE = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        log.info("TaskManager init");
        // 初始化多个查询任务
        for (int i = 0; i < QUERY_TASK_COUNT; i++) {
            QUERY_THREAD_POOL.submit(new QueryMsgTask(kdsMsgDao, branchKdsDao));
        }
    }

    public static List<QueryMsg> fetchData(String uuid) {
        return QUERY_DATA.get(uuid);
    }

    public static void setQueryData(String uuid, List<QueryMsg> queryData) {
        QUERY_DATA.put(uuid, queryData);

    }

    public static void removeData(String uuid) {
        QUERY_DATA.remove(uuid);
    }

    public static void removeWait(String uuid) {
        WAIT_QUERY.remove(uuid);
    }

    public static void tryRegisterQuery(String uuid, QueryUnit newUnit) {
        QueryUnit oldUnit = WAIT_QUERY.putIfAbsent(uuid, newUnit);
        if (oldUnit == null) {
            QUERY_QUEUE.offer(newUnit);
        }
    }

    public static BlockingQueue<QueryUnit> fetchWorkQueue() {
        return QUERY_QUEUE;
    }

    @PreDestroy
    public void shutdown() {
        log.info("TaskManager shutdown");
        QUERY_THREAD_POOL.shutdown();
    }

}
