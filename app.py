from flask import Flask, render_template, request, redirect, url_for, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask_login import LoginManager, login_user, logout_user, login_required, current_user, UserMixin
from datetime import datetime
from sqlalchemy.exc import IntegrityError
import os

app = Flask(__name__)
app.config['SECRET_KEY'] = os.environ.get('SECRET_KEY', 'your_secret_key')
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('DATABASE_URL', 'mysql+pymysql://root:password@localhost/voting_db')
app.config['SESSION_COOKIE_SECURE'] = True  # Enable in production for HTTPS
db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
login_manager = LoginManager(app)
login_manager.login_view = 'login'

# Database Models
class User(UserMixin, db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    is_admin = db.Column(db.Boolean, default=False)

class Election(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)

class Candidate(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    election_id = db.Column(db.Integer, db.ForeignKey('election.id'), nullable=False)

class Vote(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    election_id = db.Column(db.Integer, db.ForeignKey('election.id'), nullable=False)
    candidate_id = db.Column(db.Integer, db.ForeignKey('candidate.id'), nullable=False)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    __table_args__ = (db.UniqueConstraint('user_id', 'election_id', name='unique_vote'),)

@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))

# Routes
@app.route('/')
def home():
    return render_template('index.html')

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        name = request.form['name']
        email = request.form['email']
        password = request.form['password']
        hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
        user = User(name=name, email=email, password_hash=hashed_password)
        db.session.add(user)
        db.session.commit()
        return redirect(url_for('login'))
    return render_template('register.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        user = User.query.filter_by(email=email).first()
        if user and bcrypt.check_password_hash(user.password_hash, password):
            login_user(user)
            return redirect(url_for('elections'))
        else:
            return 'Invalid credentials'
    return render_template('login.html')

@app.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('home'))

@app.route('/elections')
@login_required
def elections():
    return render_template('elections.html')

@app.route('/api/elections', methods=['GET'])
@login_required
def api_elections():
    elections = Election.query.all()
    return jsonify([{
        'id': e.id,
        'name': e.name,
        'start_time': e.start_time.isoformat(),
        'end_time': e.end_time.isoformat()
    } for e in elections])

@app.route('/vote/<int:election_id>')
@login_required
def vote(election_id):
    return render_template('vote.html', election_id=election_id)

@app.route('/api/elections/<int:election_id>/candidates', methods=['GET'])
@login_required
def api_candidates(election_id):
    candidates = Candidate.query.filter_by(election_id=election_id).all()
    return jsonify([{
        'id': c.id,
        'name': c.name
    } for c in candidates])

@app.route('/api/vote', methods=['POST'])
@login_required
def api_vote():
    data = request.get_json()
    election_id = data['election_id']
    candidate_id = data['candidate_id']
    existing_vote = Vote.query.filter_by(user_id=current_user.id, election_id=election_id).first()
    if existing_vote:
        return jsonify({'message': 'You have already voted in this election'}), 403
    election = Election.query.get(election_id)
    now = datetime.utcnow()
    if now < election.start_time or now > election.end_time:
        return jsonify({'message': 'Election is not active'}), 403
    vote = Vote(user_id=current_user.id, election_id=election_id, candidate_id=candidate_id)
    db.session.add(vote)
    try:
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
        return jsonify({'message': 'Vote failed'}), 500
    return jsonify({'message': 'Vote recorded successfully'}), 200

@app.route('/results/<int:election_id>')
@login_required
def results(election_id):
    return render_template('results.html', election_id=election_id)

@app.route('/api/elections/<int:election_id>/results', methods=['GET'])
@login_required
def api_results(election_id):
    election = Election.query.get(election_id)
    if datetime.utcnow() < election.end_time:
        return jsonify({'message': 'Election not yet ended'}), 403
    votes = Vote.query.filter_by(election_id=election_id).all()
    results = {}
    for vote in votes:
        candidate = Candidate.query.get(vote.candidate_id)
        if candidate.name in results:
            results[candidate.name] += 1
        else:
            results[candidate.name] = 1
    return jsonify(results)

@app.route('/admin')
@login_required
def admin():
    if not current_user.is_admin:
        return 'Access denied', 403
    return render_template('admin.html')

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(debug=True)
