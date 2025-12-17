# Online Voting System (Java Edition)

A secure, web-based online voting system built with Java Servlets, REST API, and MySQL.

## Features
- **User Authentication**: Register/Login with BCrypt password hashing.
- **REST API**: Backend endpoints serving JSON.
- **Frontend**: Static HTML/JS Single Page Application (SPA)-lite.
- **Elections**: Admin creates elections; Users vote.
- **Results**: Real-time aggregation of votes.

## Tech Stack
- **Backend**: Java 21, Jakarta Servlet API 6.0
- **Database**: MySQL 8.0
- **Build**: Maven
- **Frontend**: HTML5, Bootstrap 4, Vanilla JavaScript

## Setup & Run

### Prerequisites
- Java 21+
- Maven
- MySQL Database

### Database Setup
1. Create a MySQL database named `voting_db`.
2. Run the schema script located at `src/main/resources/schema.sql`.

### Configuration
Set the following environment variables (or rely on defaults):
- `DATABASE_URL`: JDBC URL (default: `jdbc:mysql://localhost:3306/voting_db`)
- `DB_USER`: Database user (default: `root`)
- `DB_PASSWORD`: Database password (default: `password`)

### Build & Run
1. Build the WAR file:
   ```bash
   mvn clean package
   ```
2. Deploy the generated WAR (`target/online-voting-system-1.0-SNAPSHOT.war`) to a Servlet container like Tomcat or Jetty.

   **Or using Jetty Maven Plugin (if added):**
   ```bash
   mvn jetty:run
   ```

## Project Structure
```
src/
  main/
    java/com/voting/
      controller/   # REST API Servlets
      dao/          # Data Access Objects (JDBC)
      model/        # Data Models
      util/         # DB Connection
    webapp/         # HTML/JS Frontend
      js/           # JavaScript logic
```
