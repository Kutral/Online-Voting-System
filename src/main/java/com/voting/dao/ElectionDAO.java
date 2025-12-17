package com.voting.dao;

import com.voting.model.Election;
import com.voting.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ElectionDAO {

    public List<Election> findAll() throws SQLException {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM election";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                elections.add(mapElection(rs));
            }
        }
        return elections;
    }

    public Election findById(int id) throws SQLException {
        String sql = "SELECT * FROM election WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapElection(rs);
            }
        }
        return null;
    }

    public void create(Election election) throws SQLException {
        String sql = "INSERT INTO election (name, start_time, end_time) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, election.getName());
            stmt.setTimestamp(2, Timestamp.valueOf(election.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(election.getEndTime()));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                election.setId(rs.getInt(1));
            }
        }
    }

    private Election mapElection(ResultSet rs) throws SQLException {
        return new Election(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getTimestamp("start_time").toLocalDateTime(),
            rs.getTimestamp("end_time").toLocalDateTime()
        );
    }
}
