package com.voting.dao;

import com.voting.model.Vote;
import com.voting.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoteDAO {

    public boolean hasVoted(int userId, int electionId) throws SQLException {
        String sql = "SELECT id FROM vote WHERE user_id = ? AND election_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, electionId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public void castVote(Vote vote) throws SQLException {
        String sql = "INSERT INTO vote (user_id, election_id, candidate_id, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, vote.getUserId());
            stmt.setInt(2, vote.getElectionId());
            stmt.setInt(3, vote.getCandidateId());
            if (vote.getTimestamp() == null) {
                stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(vote.getTimestamp()));
            }
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                vote.setId(rs.getInt(1));
            }
        }
    }

    public List<Vote> findByElectionId(int electionId) throws SQLException {
        List<Vote> votes = new ArrayList<>();
        String sql = "SELECT * FROM vote WHERE election_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                votes.add(mapVote(rs));
            }
        }
        return votes;
    }

    public java.util.Map<String, Integer> getResults(int electionId) throws SQLException {
        java.util.Map<String, Integer> results = new java.util.HashMap<>();
        String sql = "SELECT c.name, COUNT(v.id) as vote_count " +
                     "FROM candidate c " +
                     "LEFT JOIN vote v ON c.id = v.candidate_id " +
                     "WHERE c.election_id = ? " +
                     "GROUP BY c.id, c.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.put(rs.getString("name"), rs.getInt("vote_count"));
            }
        }
        return results;
    }

    private Vote mapVote(ResultSet rs) throws SQLException {
        return new Vote(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("election_id"),
            rs.getInt("candidate_id"),
            rs.getTimestamp("timestamp").toLocalDateTime()
        );
    }
}
