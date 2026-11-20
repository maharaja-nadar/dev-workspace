const BASE = "http://localhost:8080/api";
const TOKEN_KEY = "dw_token";
const USER_KEY = "dw_user";

export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const getUsername = () => localStorage.getItem(USER_KEY);

export function saveSession({ token, username }) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, username);
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

const FALLBACK = {
  400: "Please check what you entered.",
  401: "Wrong username or password, or your session expired.",
  403: "You do not have permission to do that.",
  404: "Not found.",
  409: "That already exists.",
};

async function request(path, options = {}) {
  const headers = { "Content-Type": "application/json" };
  const token = getToken();
  if (token) headers.Authorization = "Bearer " + token;

  let res;
  try {
    res = await fetch(BASE + path, { ...options, headers });
  } catch {
    throw new Error("Cannot reach the server. Is the backend running?");
  }

  if (!res.ok) {
    if (res.status === 401 && token) {
      clearSession();            // token expired or invalid
      window.location.reload();  // back to the login page
    }
    let message = FALLBACK[res.status] || "Request failed (" + res.status + ")";
    try {
      const body = await res.json();
      if (body.message && !body.message.startsWith("Validation failed")) {
        message = body.message;
      }
    } catch {
      // response had no JSON body
    }
    throw new Error(message);
  }
  return res.status === 204 ? null : res.json();
}

// auth
export const register = (data) =>
  request("/auth/register", { method: "POST", body: JSON.stringify(data) });
export const login = (data) =>
  request("/auth/login", { method: "POST", body: JSON.stringify(data) });

// projects
export const getProjects = () => request("/projects");
export const createProject = (data) =>
  request("/projects", { method: "POST", body: JSON.stringify(data) });

// members
export const getMembers = (projectId) => request(`/projects/${projectId}/members`);
export const addMember = (projectId, data) =>
  request(`/projects/${projectId}/members`, { method: "POST", body: JSON.stringify(data) });
export const removeMember = (projectId, username) =>
  request(`/projects/${projectId}/members/${encodeURIComponent(username)}`, { method: "DELETE" });

// tasks
export const getTasks = (projectId) => request(`/projects/${projectId}/tasks`);
export const createTask = (projectId, data) =>
  request(`/projects/${projectId}/tasks`, { method: "POST", body: JSON.stringify(data) });
export const updateStatus = (taskId, status) =>
  request(`/tasks/${taskId}/status`, { method: "PATCH", body: JSON.stringify({ status }) });
export const deleteTask = (taskId) => request(`/tasks/${taskId}`, { method: "DELETE" });

export const getEvents = (projectId) => request(`/projects/${projectId}/events`);
export const linkRepo = (projectId, repoFullName) =>
  request(`/projects/${projectId}/repo`, { method: "PUT", body: JSON.stringify({ repoFullName }) });