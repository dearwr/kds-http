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
        String sql = "insert into t_branch_kds(f_hqid, f_branchid, f_name, f_uuid, f_create_time, f_online_time) " +
                "values(?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[]{
                kds.getHqId(), kds.getBranchId(), kds.getName(), kds.getUuid(), new Date(), new Date()
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean update(BranchKds kds) {
        String sql = "update t_branch_kds set f_name=?, f_hqid=?, f_branchid=?, f_open=?, f_offline_time=?, f_online_time=?, f_version=? where f_uuid=? ";
        return jdbcTemplate.update(sql, kds.getName(), kds.getHqId(), kds.getBranchId(), kds.isOpen(), kds.getOffLineTime(), new Date(), kds.getVersion(), kds.getUuid()) > 0;
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
        String sql = "select f_uuid from t_branch_kds where f_hqid=? and f_branchid=? and f_open=1 ";
        return jdbcTemplate.queryForList(sql, String.class, hqId, branchId);
    }

    public List<String[]> queryByHqId(int hqId, int size) {
        String sql = "select f_branchid, f_uuid from t_branch_kds where f_hqid=? and f_open=1";
        if (size > 0) {
            sql += " limit 0,?";
            return jdbcTemplate.query(sql, (set, i) -> {
                String[] fields = new String[2];
                fields[0] = set.getString("f_branchid");
                fields[1] = set.getString("f_uuid");
                return fields;
            }, hqId, size);
        }
        return jdbcTemplate.query(sql, (set, i) -> {
            String[] fields = new String[2];
            fields[0] = set.getString("f_branchid");
            fields[1] = set.getString("f_uuid");
            return fields;
        }, hqId);
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

}
