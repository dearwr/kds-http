package com.hchc.kdshttp.dao;

import com.hchc.kdshttp.mode.HqFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<HqFeature> queryWeChatQueueEnable(long hqId) {
        String sql = "SELECT * from t_hqfeature where f_hqid = ? and f_type = 'WECHAT_QUEUEING' ";
        return jdbcTemplate.query(sql, this::queryMapping, hqId);
    }

    public boolean addWeChatQueueEnable(long hqId, String uuid) {
        String sql = "insert into t_hqfeature(f_code, f_name, f_type, f_hqid, f_branchid, f_status, f_createtime, f_updatetime, f_value) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, uuid, "小程序排队", "WECHAT_QUEUEING", hqId, 0, "VALID", new Date(), new Date(), 11) > 0;
    }

    public int updateWeChatEnable(long hqId) {
        String sql = "update  t_hqfeature set f_status = 'VALID' where f_hqid = ? and f_type = 'WECHAT_QUEUEING' ";
        return jdbcTemplate.update(sql, hqId);
    }

    private HqFeature queryMapping(ResultSet rs, int i) throws SQLException {
        HqFeature feature = new HqFeature();
        feature.setHqId(rs.getLong("f_hqid"));
        feature.setStatus(rs.getString("f_status"));
        return feature;
    }

}
