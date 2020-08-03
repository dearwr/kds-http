package com.hchc.kdshttp.dao;

import com.hchc.kdshttp.entity.KdsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Repository
@Slf4j
public class KdsMsgDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void batchAdd(List<KdsMessage> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        String sql = "insert into t_kds_message(f_message_id, f_branchid, f_uuid, f_order_no, f_data, f_log_action, f_create_time, f_type, f_push_status, f_pushed_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> paramList = new ArrayList<>();
        Object[] param;
        for (KdsMessage msg : messages) {
            param = new Object[]{
                    msg.getMessageId(), msg.getBranchId(), msg.getUuid(), msg.getOrderNo(), msg.getData(), msg.getLogAction(), new Date(), msg.getType(),
                    msg.isPushed(), msg.getPushedTime()
            };
            paramList.add(param);
        }
        jdbcTemplate.batchUpdate(sql, paramList);
    }

    public boolean updatePushed(List<String> msgIds) {
        String msgIdStr = String.join("','", msgIds);
        String sql = "update t_kds_message set f_push_status=1 , f_pushed_time=? where f_message_id in ('" + msgIdStr + "')";
        return jdbcTemplate.update(sql, new Date()) > 0;
    }

    public boolean updateInvalidMsg(String orderNo, List<String> logActions) {
        String logActionsStr = String.join("','", logActions);
        String sql = "update t_kds_message set f_status=0 , f_invalid_time=? where f_order_no=? and f_status=1 and f_log_action in ('" + logActionsStr + "')";
        return jdbcTemplate.update(sql, new Date(), orderNo) > 0;
    }

    public boolean updateInvalidMsg(String uuid, Date start, Date end) {
        String sql = "update t_kds_message set f_status=0 , f_invalid_time=? where f_uuid=? and f_create_time between ? and ? and f_status=1";
        return jdbcTemplate.update(sql, new Date(), uuid, start, end) > 0;
    }


    public List<KdsMessage> queryUnPushed(String uuid, Date startTime, int size) {
        StringBuilder sql = new StringBuilder("select f_message_id, f_order_no, f_log_action, f_data from t_kds_message where f_status=1 and f_push_status=0 ");
        List<Object> paramList = new ArrayList<>();
        if (uuid != null) {
            sql.append(" and f_uuid=? ");
            paramList.add(uuid);
        }
        if (startTime != null) {
            sql.append(" and f_create_time >= ? ");
            paramList.add(startTime);
        }
        if (size > 0) {
            sql.append(" limit ? ");
            paramList.add(size);
        }
        List<KdsMessage> messages = jdbcTemplate.query(sql.toString(), this::queryMapping, paramList.toArray());
        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }
        return messages;
    }

    private KdsMessage queryMapping(ResultSet rs, int i) throws SQLException {
        KdsMessage tkm = new KdsMessage();
        tkm.setMessageId(rs.getString("f_message_id"));
        tkm.setOrderNo(rs.getString("f_order_no"));
        tkm.setLogAction(rs.getString("f_log_action"));
        tkm.setData(rs.getString("f_data"));
        return tkm;
    }

    public List<KdsMessage> queryNewOrderMsg(String uuid, String orderNo) {
        String sql = "select * from t_kds_message where f_uuid = ? and f_order_no = ? order by f_create_time desc ";
        return jdbcTemplate.query(sql, this::queryMapping, uuid, orderNo);
    }

    public boolean queryExist(String no, String uuid, String logAction) {
        String sql = "select count(*) as total from t_kds_message where f_order_no = ? and f_uuid = ? and f_log_action = ?";
        List<Integer> countList = jdbcTemplate.query(sql, (rs, i) -> rs.getInt("total"), no, uuid, logAction);
        return !CollectionUtils.isEmpty(countList) && countList.get(0) > 0;
    }
}
