package com.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.dao.UserDAO;
import com.voting.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/auth/register", "/api/auth/login", "/api/auth/logout", "/api/auth/me"})
public class AuthServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("application/json");

        if ("/api/auth/register".equals(path)) {
            handleRegister(req, resp);
        } else if ("/api/auth/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/api/auth/logout".equals(path)) {
            handleLogout(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("application/json");

        if ("/api/auth/me".equals(path)) {
            handleMe(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<String, String> data = objectMapper.readValue(req.getReader(), Map.class);
            String name = data.get("name");
            String email = data.get("email");
            String password = data.get("password");

            if (userDAO.findByEmail(email) != null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"message\": \"Email already exists\"}");
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(0, name, email, hashedPassword, false);
            userDAO.create(user);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"User registered successfully\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"Database error\"}");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<String, String> data = objectMapper.readValue(req.getReader(), Map.class);
            String email = data.get("email");
            String password = data.get("password");

            User user = userDAO.findByEmail(email);
            if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                resp.getWriter().write("{\"message\": \"Login successful\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"message\": \"Invalid credentials\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"Database error\"}");
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.getWriter().write("{\"message\": \"Logout successful\"}");
    }

    private void handleMe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("isAdmin", user.isAdmin());
            objectMapper.writeValue(resp.getWriter(), userData);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"message\": \"Not authenticated\"}");
        }
    }
}
