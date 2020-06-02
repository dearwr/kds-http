package com.hchc.kdshttp.dao;

import com.hchc.kdshttp.entity.KdsOrder;
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

    public boolean save(KdsOrder tko) {
        String sql = "insert into t_kds_order(f_hqid, f_branchid, f_no, f_grade, f_data, f_type, f_log_action, f_completed, f_create_time, f_update_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] Params = new Object[]{
                tko.getHqId(), tko.getBranchId(), tko.getNo(), tko.getGrade(), tko.getData(), tko.getType(), tko.getLogAction(), tko.isCompleted(), tko.getCreateTime(), tko.getUpdateTime()
        };
        return jdbcTemplate.update(sql, Params) > 0;
    }

    public boolean updateOrder(KdsOrder tko) {
        String sql = "update t_kds_order set f_log_action=?, f_data=?, f_completed=?, f_update_time=? where f_id=? ";
        Object[] params = new Object[]{
                tko.getLogAction(), tko.getData(), tko.isCompleted(), new Date(), tko.getId()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public List<KdsOrder> queryUncompleted(int hqId, int branchId, Date startTime, Date endTime) {
        String sql = "select * from t_kds_order where f_hqid=? and f_branchid=? and f_completed=0 and f_create_time between ? and ?  ";
        List<KdsOrder> orders = jdbcTemplate.query(sql, this::queryMapping, hqId, branchId, startTime, endTime);
        if (CollectionUtils.isEmpty(orders)) {
            return Collections.emptyList();
        }
        return orders;
    }

    public KdsOrder query(String orderNo) {
        String sql = "select * from t_kds_order where f_no = ? ";
        List<KdsOrder> orders = jdbcTemplate.query(sql, this::queryMapping, orderNo);
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        }
        return orders.get(0);
    }

    public int deleteBeforeTime(Date endTime) {
        String sql = "delete from t_kds_order where f_create_time < ?";
        return jdbcTemplate.update(sql, endTime);
    }

    private KdsOrder queryMapping(ResultSet rs, int i) throws SQLException {
        KdsOrder tko = new KdsOrder();
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
