package com.hchc.kdshttp.dao;

import com.hchc.kdshttp.entity.KdsMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class KdsMsgDao {

    private static final Logger logger = LogManager.getLogger(KdsMsgDao.class.getName());

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
        try {
            jdbcTemplate.batchUpdate(sql, paramList);
        } catch (Exception e) {
            logger.info("[batchAdd] already exist this type msg in db ，error :{}", e.getMessage());
        }
    }

    public boolean updatePushed(List<String> msgIds) {
        String msgIdStr = String.join("','", msgIds);
        String sql = "update t_kds_message set f_push_status=1 , f_pushed_time=?, where f_message_id in ('" + msgIdStr + "') ";
        return jdbcTemplate.update(sql, new Date()) > 0;
    }

    public boolean updateInvalidMsg(String orderNo, List<String> logActions) {
        String logActionsStr = String.join("','", logActions);
        String sql = "update t_kds_message set f_status=0 , f_invalid_time=? where f_order_no=? and f_status=1 and f_log_action in ('" + logActionsStr + "')";
        return jdbcTemplate.update(sql, new Date(), orderNo) > 0;
    }

    public boolean updateInvalidMsg(int branchId, String uuid, Date start, Date end) {
        String sql = "update t_kds_message set f_status=0 , f_invalid_time=? where f_branchid=? and f_uuid=? and f_create_time between ? and ? and f_status=1";
        return jdbcTemplate.update(sql, new Date(), branchId, uuid, start, end) > 0;
    }


    public List<KdsMessage> queryUnPushed(String uuid, Date startTime, Date endTime, int size) {
        StringBuilder sql = new StringBuilder("select * from t_kds_message where f_status=1 and f_push_status=0 ");
        List<Object> paramList = new ArrayList<>();
        if (uuid != null) {
            sql.append(" and f_uuid=? ");
            paramList.add(uuid);
        }
        if (startTime != null) {
            sql.append(" and f_create_time >= ? ");
            paramList.add(startTime);
        }
        if (endTime != null) {
            sql.append(" and f_create_time < ? ");
            paramList.add(endTime);
        }
        if (size > 0) {
            sql.append(" limit 0,? ");
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
        tkm.setBranchId(rs.getInt("f_branchid"));
        tkm.setData(rs.getString("f_data"));
        tkm.setLogAction(rs.getString("f_log_action"));
        tkm.setUuid(rs.getString("f_uuid"));
        tkm.setType(rs.getString("f_type"));
        return tkm;
    }

    public List<KdsMessage> queryOrderMsg(String uuid, String orderNo) {
        String sql = "select * from t_kds_message where f_uuid = ? and f_order_no = ? order by f_create_time desc ";
        return jdbcTemplate.query(sql, this::queryMapping, uuid, orderNo);
    }

    public int deleteBeforeTime(Date endTime) {
        String sql = "delete from t_kds_message where f_create_time < ?";
        return jdbcTemplate.update(sql, endTime);
    }

}
