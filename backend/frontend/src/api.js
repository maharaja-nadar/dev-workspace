const BASE = "http://localhost:8080/api";

async function request(path, options = {}) {
  const res = await fetch(BASE + path, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    let message = "Request failed (" + res.status + ")";
    try {
      const body = await res.json();
      if (body.message) message = body.message;
    } catch {
      // response had no JSON body
    }
    throw new Error(message);
  }
  return res.status === 204 ? null : res.json();
}

export const getProjects = () => request("/projects");

export const createProject = (data) =>
  request("/projects", { method: "POST", body: JSON.stringify(data) });

export const getTasks = (projectId) => request(`/projects/${projectId}/tasks`);

export const createTask = (projectId, data) =>
  request(`/projects/${projectId}/tasks`, {
    method: "POST",
    body: JSON.stringify(data),
  });

export const updateStatus = (taskId, status) =>
  request(`/tasks/${taskId}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status }),
  });

export const deleteTask = (taskId) =>
  request(`/tasks/${taskId}`, { method: "DELETE" });