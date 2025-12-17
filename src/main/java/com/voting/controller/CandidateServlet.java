package com.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.dao.CandidateDAO;
import com.voting.model.Candidate;
import com.voting.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/candidates/*"})
public class CandidateServlet extends HttpServlet {

    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
             resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             return;
        }

        // Expected URL: /api/candidates/{electionId}
        try {
            int electionId = Integer.parseInt(pathInfo.substring(1));
            List<Candidate> candidates = candidateDAO.findByElectionId(electionId);
            objectMapper.writeValue(resp.getWriter(), candidates);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!user.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            Map<String, Object> data = objectMapper.readValue(req.getReader(), Map.class);
            String name = (String) data.get("name");
            int electionId = (int) data.get("election_id");

            Candidate candidate = new Candidate(0, name, electionId);
            candidateDAO.create(candidate);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Candidate added successfully\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
