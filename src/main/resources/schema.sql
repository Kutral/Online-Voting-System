CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(128) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS election (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS candidate (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    election_id INT NOT NULL,
    FOREIGN KEY (election_id) REFERENCES election(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vote (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    election_id INT NOT NULL,
    candidate_id INT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (election_id) REFERENCES election(id),
    FOREIGN KEY (candidate_id) REFERENCES candidate(id),
    UNIQUE KEY unique_vote (user_id, election_id)
);
