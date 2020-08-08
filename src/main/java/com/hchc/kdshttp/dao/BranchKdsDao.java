package com.hchc.kdshttp.dao;

import com.hchc.kdshttp.entity.BranchKds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Repository
public class BranchKdsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean add(BranchKds kds) {
        String sql = "insert into t_branch_kds(f_hqid, f_branchid, f_name, f_uuid, f_create_time, f_online_time, f_open) " +
                "values(?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[]{
                kds.getHqId(), kds.getBranchId(), kds.getName(), kds.getUuid(), new Date(), new Date(), 1
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean update(BranchKds kds) {
        String sql = "update t_branch_kds set f_name=?, f_hqid=?, f_branchid=?, f_bind=1, f_offline_time=?, f_online_time=?, f_version=?, f_open=? where f_uuid=? ";
        return jdbcTemplate.update(sql, kds.getName(), kds.getHqId(), kds.getBranchId(), kds.getOffLineTime(), new Date(), kds.getVersion(), kds.isOpen(), kds.getUuid()) > 0;
    }

    public BranchKds query(String uuid) {
        String sql = "select * from t_branch_kds where f_uuid=? ";
        List<BranchKds> kdsList = jdbcTemplate.query(sql, this::queryMapping, uuid);
        if (CollectionUtils.isEmpty(kdsList)) {
            return null;
        }
        return kdsList.get(0);
    }


    public List<String> queryUUIDs(int hqId, int branchId) {
        String sql = "select f_uuid from t_branch_kds where f_hqid=? and f_branchid=? ";
        return jdbcTemplate.queryForList(sql, String.class, hqId, branchId);
    }

    public boolean updateHeartTime(Date heartTime, String uuid) {
        String sql = "update t_branch_kds set f_heart_time=? where f_uuid=?";
        return jdbcTemplate.update(sql, heartTime, uuid) > 0;
    }

    private BranchKds queryMapping(ResultSet rs, int i) throws SQLException {
        BranchKds kds = new BranchKds();
        kds.setHqId(rs.getInt("f_hqid"));
        kds.setBranchId(rs.getInt("f_branchid"));
        kds.setUuid(rs.getString("f_uuid"));
        kds.setName(rs.getString("f_name"));
        kds.setCreateTime(rs.getTime("f_create_time"));
        kds.setOpen(rs.getBoolean("f_open"));
        kds.setOffLineTime(rs.getTime("f_offline_time"));
        kds.setOnlineTime(rs.getTime("f_online_time"));
        kds.setVersion(rs.getString("f_version"));
        return kds;
    }

    public void unBind(int branchId, String uuid) {
        String sql = "update t_branch_kds set f_bind = 0 where f_branchid = ? and f_uuid = ?";
        jdbcTemplate.update(sql, branchId, uuid);
    }

    public int queryBindKdsCount(int branchId) {
        String sql = "select count(*) from t_branch_kds where f_branchid = ? and f_bind = 1";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, branchId);
        return count == null ? 0 : count;
    }

    public boolean queryWeChatQueueEnable(int branchId) {
        String sql = "select f_open from t_branch_kds where f_branchid = ?";
        List<Integer> openList = jdbcTemplate.query(sql, (rs, i) -> rs.getInt("f_open"), branchId);
        if (CollectionUtils.isEmpty(openList)) {
            return true;
        }
        for (Integer open : openList) {
            if (open == 1) {
                return true;
            }
        }
        return false;
    }

    public void closeWeChatQueueEnable(int branchId) {
        String sql = "update t_branch_kds set f_open = 0 where f_branchid = ?";
        jdbcTemplate.update(sql, branchId);
    }
}
