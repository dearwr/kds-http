package com.hchc.kdshttp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Date;
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
        String sql = "SELECT count(*) as total from t_hqfeature where f_hqid = ? and f_type = 'WECHAT_QUEUEING' and f_status = 'VALID' ";
        List<Integer> countList = jdbcTemplate.query(sql, (rs, i) -> rs.getInt("total"), hqId);
        return !CollectionUtils.isEmpty(countList) && countList.get(0) > 0;
    }

    public boolean addWeChatQueueEnable(long hqId, String uuid) {
        String sql = "insert into t_hqfeature(f_code, f_name, f_type, f_hqid, f_branchid, f_status, f_createtime, f_updattime, f_value) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, uuid, "小程序排队", "WECHAT_QUEUEING", hqId, 0, "VALID", new Date(), new Date(), 11) > 0;
    }

}
