# app.py

from flask import Flask, render_template, request, redirect, url_for
import sqlite3

app = Flask(__name__)

# Initialize database
conn = sqlite3.connect('database.db')
c = conn.cursor()
c.execute('CREATE TABLE IF NOT EXISTS votes (id INTEGER PRIMARY KEY, candidate TEXT)')
conn.commit()
conn.close()

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/vote', methods=['POST'])
def vote():
    candidate = request.form['candidate']

    # Insert vote into the database
    conn = sqlite3.connect('database.db')
    c = conn.cursor()
    c.execute('INSERT INTO votes (candidate) VALUES (?)', (candidate,))
    conn.commit()
    conn.close()

    return redirect(url_for('index'))

@app.route('/results')
def results():
    conn = sqlite3.connect('database.db')
    c = conn.cursor()
    c.execute('SELECT candidate, COUNT(*) FROM votes GROUP BY candidate')
    results = c.fetchall()
    conn.close()

    return render_template('results.html', results=results)

if __name__ == '__main__':
    app.run(debug=True)
