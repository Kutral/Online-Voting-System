
# Online Voting System

The Online Voting System is a secure, web-based application designed to facilitate digital elections. Users can register, log in, vote in active elections, and view results, while administrators can manage elections and candidates. The application leverages modern web technologies and cloud infrastructure for scalability and reliability.

## Features
- **User Authentication**: Secure registration and login with password hashing.
- **Voting**: Users can vote in elections during active periods, with one vote per election enforced.
- **Admin Interface**: Admins can create elections and add candidates.
- **Results**: Viewable post-election with candidate vote counts.
- **Deployment**: Hosted on AWS for scalability and security.

## Tech Stack
- **Frontend**: HTML5, CSS (Bootstrap), JavaScript (Fetch API)
- **Backend**: Python (Flask framework)
- **Database**: MySQL (via SQLAlchemy)
- **Deployment**: AWS (EC2 for application, RDS for database)
- **Tools**: Gunicorn (WSGI server), Nginx (reverse proxy), Let's Encrypt (HTTPS)

## Project Structure
```
online_voting_system/
├── app.py              # Flask application with routes and logic
├── templates/          # HTML templates
│   ├── base.html
│   ├── index.html
│   ├── register.html
│   ├── login.html
│   ├── elections.html
│   ├── vote.html
│   ├── results.html
│   └── admin.html
├── static/             # Static files (CSS, JS) - optional for custom assets
├── requirements.txt    # Python dependencies
└── README.md           # This file
```

## Prerequisites
- Python 3.7+
- MySQL (local or via Docker)
- AWS account
- Git

## Local Setup
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/<your-username>/online_voting_system.git
   cd online_voting_system
   ```

2. **Set Up Virtual Environment:**
   ```bash
   python -m venv venv
   source venv/bin/activate  # Linux/Mac
   # or
   venv\Scripts\activate     # Windows
   ```

3. **Install Dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

4. **Set Up MySQL Locally:**
   - Option 1: Use Docker:
     ```bash
     docker run -d -p 3306:3306 --name mysql-db -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=voting_db mysql:5.7
     ```
   - Option 2: Install MySQL locally and create a database named `voting_db`.

5. **Configure Environment Variables:**
   Create a `.env` file (optional) or set variables directly:
   ```bash
   export SECRET_KEY='your_secret_key'
   export DATABASE_URL='mysql+pymysql://root:password@localhost/voting_db'
   ```

6. **Run the Application:**
   ```bash
   python app.py
   ```
   Access at `http://localhost:5000`.

## AWS Deployment Instructions

### Step 1: Set Up EC2 Instance
1. Launch an EC2 instance (e.g., Amazon Linux 2, t2.micro for free tier).
2. SSH into the instance:
   ```bash
   ssh -i <your-key.pem> ec2-user@<ec2-public-ip>
   ```
3. Update and install dependencies:
   ```bash
   sudo yum update -y
   sudo yum install python3 git -y
   sudo yum install python3-pip -y
   ```

4. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/online_voting_system.git
   cd online_voting_system
   ```

5. Install Python dependencies:
   ```bash
   pip3 install -r requirements.txt
   ```

### Step 2: Set Up RDS (MySQL)
1. Create a MySQL instance on AWS RDS (e.g., db.t2.micro for free tier).
2. Note the endpoint, username, password, and database name.
3. Update `app.py` configuration with environment variables on EC2:
   ```bash
   export DB_USER='admin'
   export DB_PASSWORD='your_rds_password'
   export DB_HOST='your-rds-endpoint'
   export DB_NAME='voting_db'
   ```

4. Configure EC2 security group to allow port 3306 inbound from the RDS instance.

### Step 3: Run the Application
1. Install Gunicorn:
   ```bash
   pip3 install gunicorn
   ```
2. Test the app:
   ```bash
   gunicorn -w 4 app:app
   ```

### Step 4: Configure Nginx
1. Install Nginx:
   ```bash
   sudo yum install nginx -y
   ```
2. Create a configuration file at `/etc/nginx/conf.d/voting.conf`:
   ```nginx
   server {
       listen 80;
       server_name <your-ec2-public-ip-or-domain>;

       location / {
           proxy_pass http://127.0.0.1:8000;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```
3. Restart Nginx:
   ```bash
   sudo systemctl restart nginx
   sudo systemctl enable nginx
   ```

### Step 5: Enable HTTPS with Let's Encrypt
1. Install Certbot:
   ```bash
   sudo amazon-linux-extras install epel -y
   sudo yum install certbot python2-certbot-nginx -y
   ```
2. Obtain SSL certificate:
   ```bash
   sudo certbot --nginx -d <your-domain>
   ```
   Follow prompts to configure HTTPS.

### Step 6: Automate Startup
1. Create a systemd service file at `/etc/systemd/system/voting.service`:
   ```ini
   [Unit]
   Description=Gunicorn instance for Online Voting System
   After=network.target

   [Service]
   User=ec2-user
   Group=ec2-user
   WorkingDirectory=/home/ec2-user/online_voting_system
   ExecStart=/home/ec2-user/online_voting_system/venv/bin/gunicorn -w 4 app:app
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```
2. Enable and start the service:
   ```bash
   sudo systemctl enable voting
   sudo systemctl start voting
   ```

### Step 7: Finalize Security
- Update EC2 security group: Allow inbound traffic on ports 80 (HTTP) and 443 (HTTPS).
- Update RDS security group: Allow inbound traffic on port 3306 from EC2 instance only.
- Store sensitive data (e.g., `SECRET_KEY`, database credentials) in AWS Secrets Manager or environment variables.

## Usage
1. Access the app at `http://<ec2-public-ip>` or `https://<your-domain>` after HTTPS setup.
2. Register and log in as a user to vote.
3. Log in with an admin account (set `is_admin=True` in the database manually for a user) to manage elections.

## Contributing
Feel free to fork this repository, submit issues, or create pull requests to enhance the project.

## License
This project is licensed under the MIT License.
```

---

## Notes
- **SQL Usage**: Confirmed with MySQL and SQLAlchemy ORM in the codebase.
- **Admin Page**: Now fully functional with forms to create elections and add candidates.
- **README**: Includes everything needed for GitHub—description, setup, and detailed AWS deployment steps.

You can now upload this to GitHub by creating a `requirements.txt` file with:

```plaintext
flask
flask-sqlalchemy
flask-bcrypt
flask-login
pymysql
gunicorn
```

Then, push to your repository:
```bash
git init
git add .
git commit -m "Initial commit of Online Voting System"
git remote add origin https://github.com/<your-username>/online_voting_system.git
git push -u origin main
```

Let me know if you need further adjustments!
