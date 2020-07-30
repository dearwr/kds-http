package com.hchc.kdshttp.dao;

import com.hchc.kdshttp.entity.TKdsOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Repository
public class KdsOrderDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean updateOrder(TKdsOrder tko) {
        String sql = "update t_kds_order set f_log_action=?, f_data=?, f_completed=?, f_update_time=? where f_id=? ";
        Object[] params = new Object[]{
                tko.getLogAction(), tko.getData(), tko.isCompleted(), new Date(), tko.getId()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public void updateCallTime(String orderNo) {
        String sql = "update t_kds_order set f_call_time = ? where f_no = ? ";
        jdbcTemplate.update(sql, new Date(), orderNo);
    }

    public List<TKdsOrder> queryUncompleted(int hqId, int branchId, Date startTime) {
        String sql = "select * from t_kds_order where f_hqid=? and f_branchid=? and f_completed=0 and f_create_time > ? ";
        List<TKdsOrder> orders = jdbcTemplate.query(sql, this::queryMapping, hqId, branchId, startTime);
        if (CollectionUtils.isEmpty(orders)) {
            return Collections.emptyList();
        }
        return orders;
    }

    public TKdsOrder query(String orderNo) {
        String sql = "select * from t_kds_order where f_no = ? ";
        List<TKdsOrder> orders = jdbcTemplate.query(sql, this::queryMapping, orderNo);
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        }
        return orders.get(0);
    }

    private TKdsOrder queryMapping(ResultSet rs, int i) throws SQLException {
        TKdsOrder tko = new TKdsOrder();
        tko.setId(rs.getInt("f_id"));
        tko.setHqId(rs.getInt("f_hqid"));
        tko.setBranchId(rs.getInt("f_branchid"));
        tko.setNo(rs.getString("f_no"));
        tko.setGrade(rs.getString("f_grade"));
        tko.setType(rs.getString("f_type"));
        tko.setData(rs.getString("f_data"));
        tko.setLogAction(rs.getString("f_log_action"));
        return tko;
    }

}
