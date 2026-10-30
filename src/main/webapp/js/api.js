// Relatieve URL: werkt zowel lokaal (localhost:8080/library/) als op de VM.
const API = 'api';

function getSession() {
  try { return JSON.parse(sessionStorage.getItem('session')); } catch (e) { return null; }
}

function requireLogin(role) {
  const s = getSession();
  if (!s) { location.href = 'index.html'; return null; }
  if (role && s.role !== role) { location.href = 'items.html'; return null; }
  return s;
}

function logout() {
  sessionStorage.removeItem('session');
  location.href = 'index.html';
}

async function apiFetch(path, options = {}) {
  const s = getSession();
  const headers = { 'Content-Type': 'application/json' };
  if (s) headers['Authorization'] = 'Bearer ' + s.token;
  const res = await fetch(API + path, { ...options, headers });
  if (res.status === 401 && path !== '/auth/login') { logout(); throw new Error('Sessie verlopen'); }
  const text = await res.text();
  let body = null;
  if (text) { try { body = JSON.parse(text); } catch (e) { body = null; } }
  if (!res.ok) throw new Error((body && body.error) || ('Fout ' + res.status));
  return body;
}

// Hulpfunctie die tekst via textContent zet (voorkomt XSS via innerHTML).
function el(tag, text, className) {
  const e = document.createElement(tag);
  if (text !== undefined && text !== null) e.textContent = text;
  if (className) e.className = className;
  return e;
}

function showMessage(text, type) {
  const m = document.getElementById('msg');
  m.textContent = text;
  m.className = 'msg ' + type;
}

function renderNav(active) {
  const s = getSession();
  const nav = document.getElementById('nav');
  const links = [['items.html', 'Items', 'items'],
                 ['my-loans.html', s.role === 'LIBRARIAN' ? 'Alle leningen' : 'Mijn leningen', 'loans']];
  if (s.role === 'LIBRARIAN') links.push(['manage.html', 'Beheer', 'manage']);
  links.forEach(([href, label, key]) => {
    const a = el('a', label);
    a.href = href;
    if (key === active) a.className = 'active';
    nav.appendChild(a);
  });
  nav.appendChild(el('span', null, 'spacer'));
  nav.appendChild(el('span', s.username + ' (' + s.role + ')'));
  const b = el('button', 'Uitloggen');
  b.onclick = logout;
  nav.appendChild(b);
}
