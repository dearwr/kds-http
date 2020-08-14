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
        String sql = "update t_kds_order set f_log_action=?, f_data=?, f_completed=?, f_update_time=?, f_call_time=?, f_make_time=?, f_book_time=? where f_id=? ";
        Object[] params = new Object[]{
                tko.getLogAction(), tko.getData(), tko.isCompleted(), new Date(), tko.getCallTime(), tko.getMakeTime(), tko.getBookTime(), tko.getId()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public void updateCallTime(String orderNo) {
        String sql = "update t_kds_order set f_call_time = ? where f_no = ? ";
        jdbcTemplate.update(sql, new Date(), orderNo);
    }

    public List<TKdsOrder> queryUncompleted(int hqId, int branchId, Date startTime, String types) {
        String sql = "select * from t_kds_order where f_hqid=? and f_branchid=? and f_completed=0 and f_create_time > ? and f_type in ( " + types + ")";
        List<TKdsOrder> orders = jdbcTemplate.query(sql, this::queryMapping, hqId, branchId, startTime);
        if (CollectionUtils.isEmpty(orders)) {
            return Collections.emptyList();
        }
        return orders;
    }

    public List<TKdsOrder> queryAllUncompleted(int hqId, int branchId, String types) {
        String sql = "select * from t_kds_order where f_hqid=? and f_branchid=? and f_completed=0 and f_type in ( " + types + ")";
        List<TKdsOrder> orders = jdbcTemplate.query(sql, this::queryMapping, hqId, branchId);
        if (CollectionUtils.isEmpty(orders)) {
            return Collections.emptyList();
        }
        return orders;
    }

    public TKdsOrder query(String orderNo) {
        String sql = "select * from t_kds_order where f_no = ? and f_type in (?, ?, ?, ?)";
        List<TKdsOrder> orders = jdbcTemplate.query(sql, this::queryMapping, orderNo, "STORE", "MALL", "ONLINE", "DELIVERY");
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
        tko.setMakeTime(rs.getDate("f_make_time"));
        tko.setCallTime(rs.getDate("f_call_time"));
        tko.setBookTime(rs.getString("f_book_time"));
        return tko;
    }

    public void completeOrders(int branchId, Date start, Date end) {
        String sql = "update t_kds_order set f_log_action = 'TAKE_COMPLETE' where f_branchid = ? " +
                "and f_create_time >= ? and f_create_time < ? and f_log_action in (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, branchId, start, end, "ORDER_NEW", "ORDER_PRE", "ORDER_MAKE", "ORDER_CALL", "ORDER_DELIVERYING");
    }
}
