package com.voting.dao;

import com.voting.model.Candidate;
import com.voting.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {

    public List<Candidate> findByElectionId(int electionId) throws SQLException {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT * FROM candidate WHERE election_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                candidates.add(mapCandidate(rs));
            }
        }
        return candidates;
    }

    public Candidate findById(int id) throws SQLException {
        String sql = "SELECT * FROM candidate WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCandidate(rs);
            }
        }
        return null;
    }

    public void create(Candidate candidate) throws SQLException {
        String sql = "INSERT INTO candidate (name, election_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, candidate.getName());
            stmt.setInt(2, candidate.getElectionId());
            stmt.executeUpdate();

             ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                candidate.setId(rs.getInt(1));
            }
        }
    }

    private Candidate mapCandidate(ResultSet rs) throws SQLException {
        return new Candidate(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("election_id")
        );
    }
}
