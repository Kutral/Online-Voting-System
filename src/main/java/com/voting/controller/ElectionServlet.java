package com.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.voting.dao.ElectionDAO;
import com.voting.model.Election;
import com.voting.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/elections"})
public class ElectionServlet extends HttpServlet {

    private final ElectionDAO electionDAO = new ElectionDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ElectionServlet() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            List<Election> elections = electionDAO.findAll();
            objectMapper.writeValue(resp.getWriter(), elections);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"Database error\"}");
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
            Map<String, String> data = objectMapper.readValue(req.getReader(), Map.class);
            String name = data.get("name");
            LocalDateTime startTime = LocalDateTime.parse(data.get("start_time"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime endTime = LocalDateTime.parse(data.get("end_time"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Election election = new Election(0, name, startTime, endTime);
            electionDAO.create(election);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Election created successfully\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"Database error\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\": \"Invalid data format\"}");
        }
    }
}
