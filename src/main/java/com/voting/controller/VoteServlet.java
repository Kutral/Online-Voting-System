package com.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.dao.ElectionDAO;
import com.voting.dao.VoteDAO;
import com.voting.model.Election;
import com.voting.model.User;
import com.voting.model.Vote;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/vote"})
public class VoteServlet extends HttpServlet {

    private final VoteDAO voteDAO = new VoteDAO();
    private final ElectionDAO electionDAO = new ElectionDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"message\": \"Login required\"}");
            return;
        }
        User user = (User) session.getAttribute("user");

        try {
            Map<String, Integer> data = objectMapper.readValue(req.getReader(), Map.class);
            int electionId = data.get("election_id");
            int candidateId = data.get("candidate_id");

            if (voteDAO.hasVoted(user.getId(), electionId)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"message\": \"You have already voted in this election\"}");
                return;
            }

            Election election = electionDAO.findById(electionId);
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(election.getStartTime()) || now.isAfter(election.getEndTime())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"message\": \"Election is not active\"}");
                return;
            }

            Vote vote = new Vote(0, user.getId(), electionId, candidateId, now);
            voteDAO.castVote(vote);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"Vote recorded successfully\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"Database error\"}");
        }
    }
}
