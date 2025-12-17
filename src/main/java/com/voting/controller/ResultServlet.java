package com.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.dao.CandidateDAO;
import com.voting.dao.ElectionDAO;
import com.voting.dao.VoteDAO;
import com.voting.model.Candidate;
import com.voting.model.Election;
import com.voting.model.Vote;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/results/*"})
public class ResultServlet extends HttpServlet {

    private final VoteDAO voteDAO = new VoteDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final ElectionDAO electionDAO = new ElectionDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
             resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             return;
        }

        // Expected URL: /api/results/{electionId}
        try {
            int electionId = Integer.parseInt(pathInfo.substring(1));

            Election election = electionDAO.findById(electionId);
             if (LocalDateTime.now().isBefore(election.getEndTime())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"message\": \"Election not yet ended\"}");
                return;
            }

            Map<String, Integer> results = voteDAO.getResults(electionId);
            objectMapper.writeValue(resp.getWriter(), results);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
