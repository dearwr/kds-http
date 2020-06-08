package com.hchc.kdshttp;

import com.hchc.kdshttp.dao.BranchKdsDao;
import com.hchc.kdshttp.pack.Result;
import com.hchc.kdshttp.util.DatetimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
class KdsHttpApplicationTests {

    private static final String LOOP_QUERY_URL = "http://lab2.51hchc.com:4517/kds/loopQuery";

    @Autowired
    private BranchKdsDao branchKdsDao;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        List<String[]> kdsList = branchKdsDao.queryByHqId(2439,100);
        int size = kdsList.size();
		ExecutorService executor = Executors.newFixedThreadPool(size);
        System.out.println("start" + DatetimeUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        for (String[] kds : kdsList) {
            executor.execute(() -> {
                int count = 1;
                while (count < 30) {
                    String queryUrl = LOOP_QUERY_URL + "?branchId=" + kds[0] + "&uuid=" + kds[1];
                    Result result = restTemplate.getForEntity(queryUrl, Result.class).getBody();
                    if (result.getCode() == 0) {
                        System.out.println("branchId:" + kds[0] + "第" + (count++) + "次查询");
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            while (true) {
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
