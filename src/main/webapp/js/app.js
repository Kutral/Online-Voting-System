// Global Auth State
let currentUser = null;

async function checkAuth() {
    try {
        const response = await fetch('/api/auth/me');
        if (response.ok) {
            currentUser = await response.json();
        } else {
            currentUser = null;
        }
        updateNav();
    } catch (e) {
        console.error(e);
    }
}

function updateNav() {
    const nav = document.getElementById('nav-links');
    if (!nav) return; // Might not be on a page with nav?

    nav.innerHTML = '';
    if (currentUser) {
        const electionsLi = document.createElement('li');
        electionsLi.className = 'nav-item';
        electionsLi.innerHTML = '<a class="nav-link" href="/elections.html">Elections</a>';
        nav.appendChild(electionsLi);

        if (currentUser.isAdmin) {
             const adminLi = document.createElement('li');
             adminLi.className = 'nav-item';
             adminLi.innerHTML = '<a class="nav-link" href="/admin.html">Admin</a>';
             nav.appendChild(adminLi);
        }

        const logoutLi = document.createElement('li');
        logoutLi.className = 'nav-item';
        logoutLi.innerHTML = '<a class="nav-link" href="#" onclick="logout()">Logout</a>';
        nav.appendChild(logoutLi);

    } else {
        const loginLi = document.createElement('li');
        loginLi.className = 'nav-item';
        loginLi.innerHTML = '<a class="nav-link" href="/login.html">Login</a>';
        nav.appendChild(loginLi);

        const registerLi = document.createElement('li');
        registerLi.className = 'nav-item';
        registerLi.innerHTML = '<a class="nav-link" href="/register.html">Register</a>';
        nav.appendChild(registerLi);
    }
}

async function logout() {
    await fetch('/api/auth/logout', { method: 'POST' });
    window.location.href = '/';
}

document.addEventListener('DOMContentLoaded', checkAuth);
