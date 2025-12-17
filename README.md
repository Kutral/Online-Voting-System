# 🗳️ ElectionPal - Online Voting System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Active-brightgreen.svg)]()

**ElectionPal** is a secure, modern, and scalable online voting platform engineered with pure Java architectures. It transitions away from heavy frameworks to provide a high-performance, lightweight REST API backed by robust MySQL persistence.

---

## 🚀 Key Features

*   **🔐 Secure Authentication**: Robust user management system using **BCrypt** hashing for industry-standard password security.
*   **🗳️ Voting Engine**: Real-time voting capability with strict one-person-one-vote enforcement.
*   **📊 Live Results**: Instantaneous vote aggregation and result visualization powered by optimized SQL queries.
*   **⚡ RESTful API**: A clean, JSON-based API architecture decoupling the backend from the frontend.
*   **📱 Responsive Frontend**: A lightweight Single Page Application (SPA) built with Bootstrap 4 and Vanilla JS for a seamless user experience.
*   **👨‍💼 Admin Dashboard**: comprehensive tools for election commissioners to manage elections and candidates.

---

## 🛠️ Technology Stack

| Component | Technology | Description |
|-----------|------------|-------------|
| **Backend** | Java 21 | Core logic using Jakarta EE Servlet API 6.0 |
| **Database** | MySQL 8.0 | Relational data persistence |
| **Data Access** | JDBC + HikariCP | High-performance database connection pooling |
| **Build Tool** | Maven | Dependency management and build automation |
| **Frontend** | HTML5 / JS | Client-side rendering with Fetch API |
| **Styling** | Bootstrap 4 | Responsive grid layout and components |

---

## 🏗️ Architecture

The project follows a clean **MVC (Model-View-Controller)** separation (with the View decoupled as a static client):

```
src/
└── main/
    ├── java/com/voting/
    │   ├── controller/   # 🎮 Servlets handling HTTP REST requests
    │   ├── dao/          # 💾 Data Access Objects (JDBC implementation)
    │   ├── model/        # 📦 POJO Data Models
    │   ├── util/         # ⚙️ Utilities (DB Connection Pool)
    │   └── filter/       # 🛡️ Security Filters (Auth, CORS)
    └── webapp/
        ├── js/           # ⚡ Client-side Application Logic
        └── *.html        # 🖼️ Static Views
```

---

## 🔌 API Documentation

### Authentication
*   `POST /api/auth/register` - Create a new user account.
*   `POST /api/auth/login` - Authenticate and create a session.
*   `GET  /api/auth/me` - Retrieve current session user details.
*   `POST /api/auth/logout` - Invalidate the current session.

### Elections
*   `GET  /api/elections` - List all active and past elections.
*   `POST /api/elections` - (Admin) Create a new election.
*   `GET  /api/elections/{id}/candidates` - Get candidates for a specific election.
*   `POST /api/candidates` - (Admin) Add a candidate to an election.

### Voting
*   `POST /api/vote` - Cast a vote (Requires Auth).
*   `GET  /api/results/{id}` - Get real-time results for an election.

---

## 💻 Setup & Installation

### Prerequisites
*   Java Development Kit (JDK) 21 or higher
*   Apache Maven
*   MySQL Server

### 1. Database Configuration
Create a database and initialize the schema:

```sql
CREATE DATABASE voting_db;
USE voting_db;
-- Execute the contents of src/main/resources/schema.sql
```

### 2. Environment Variables
Configure the application using environment variables (defaults shown):

```bash
export DATABASE_URL='jdbc:mysql://localhost:3306/voting_db'
export DB_USER='root'
export DB_PASSWORD='password'
```

### 3. Build & Run
Compile the application and package it into a WAR file:

```bash
mvn clean package
```

Deploy the `target/online-voting-system-1.0-SNAPSHOT.war` to your favorite Servlet Container (Tomcat, Jetty, WildFly).

---

## 🤝 Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
