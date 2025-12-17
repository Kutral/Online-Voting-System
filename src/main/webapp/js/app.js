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
        nav.innerHTML += `<li class="nav-item"><a class="nav-link" href="/elections.html">Elections</a></li>`;
        if (currentUser.isAdmin) {
            nav.innerHTML += `<li class="nav-item"><a class="nav-link" href="/admin.html">Admin</a></li>`;
        }
        nav.innerHTML += `<li class="nav-item"><a class="nav-link" href="#" onclick="logout()">Logout</a></li>`;
    } else {
        nav.innerHTML += `<li class="nav-item"><a class="nav-link" href="/login.html">Login</a></li>`;
        nav.innerHTML += `<li class="nav-item"><a class="nav-link" href="/register.html">Register</a></li>`;
    }
}

async function logout() {
    await fetch('/api/auth/logout', { method: 'POST' });
    window.location.href = '/';
}

document.addEventListener('DOMContentLoaded', checkAuth);
