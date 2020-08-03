package com.hchc.kdshttp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-08-03
 */
@Repository
public class HqFeatureDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean queryWeChatQueueEnable(long hqId) {
        String sql = "SELECT count(*) from t_hqfeature where f_hqid = ? and f_type = 'WECHAT_QUEUEING' and f_status = 'VALID' ";
        List<Integer> count = jdbcTemplate.query(sql, (rs, i) -> rs.getInt(1), hqId);
        return !CollectionUtils.isEmpty(count) && count.size() != 0;
    }
}
