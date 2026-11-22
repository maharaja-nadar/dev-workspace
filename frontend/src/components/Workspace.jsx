import { useEffect, useState } from "react";
import { getProjects, createProject, getMembers, getUsername } from "../api";
import Board from "./Board";
import MembersPanel from "./MembersPanel";
import RepoPanel from "./RepoPanel";
import DeploymentsPanel from "./DeploymentsPanel";

export default function Workspace({ onLogout }) {
  const me = getUsername();
  const [projects, setProjects] = useState([]);
  const [projectId, setProjectId] = useState(null);
  const [members, setMembers] = useState([]);
  const [error, setError] = useState("");
  const [name, setName] = useState("");
  const [keyPrefix, setKeyPrefix] = useState("");

  async function loadProjects(selectId) {
    try {
      const list = await getProjects();
      setProjects(list);
      if (selectId) setProjectId(selectId);
      else if (list.length > 0 && projectId === null) setProjectId(list[0].id);
    } catch (e) {
      setError(e.message);
    }
  }

  async function loadMembers() {
    if (projectId === null) return;
    try {
      setMembers(await getMembers(projectId));
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    loadProjects();
  }, []);

  useEffect(() => {
    loadMembers();
  }, [projectId]);

  async function handleCreateProject(e) {
    e.preventDefault();
    setError("");
    try {
      const created = await createProject({ name, keyPrefix: keyPrefix.toUpperCase() });
      setName("");
      setKeyPrefix("");
      await loadProjects(created.id);
    } catch (err) {
      setError(err.message);
    }
  }

  const isLead = members.some((m) => m.username === me && m.role === "LEAD");
  const project = projects.find((p) => p.id === projectId);

  return (
    <div className="app">
      <header className="top">
        <h1>Dev Vectra</h1>
        <div className="top-right">
          {projects.length > 0 && (
            <select
              value={projectId ?? ""}
              onChange={(e) => setProjectId(Number(e.target.value))}
              aria-label="Select project"
            >
              {projects.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name} ({p.keyPrefix})
                </option>
              ))}
            </select>
          )}
          <span className="who">Signed in as {me}</span>
          <button className="btn" onClick={onLogout}>
            Log out
          </button>
        </div>
      </header>

      {error && (
        <p className="error" role="alert">
          {error}
        </p>
      )}

      <form className="inline-form" onSubmit={handleCreateProject}>
        <input
          placeholder="New project name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <input
          placeholder="Key (e.g. WS)"
          value={keyPrefix}
          onChange={(e) => setKeyPrefix(e.target.value)}
          maxLength={5}
          required
        />
        <button className="btn">Add project</button>
      </form>

      {projectId ? (
        <>
          <MembersPanel
            projectId={projectId}
            members={members}
            isLead={isLead}
            onChanged={loadMembers}
            onError={setError}
          />
          {project && (
            <RepoPanel
              project={project}
              isLead={isLead}
              onLinked={() => loadProjects(projectId)}
              onError={setError}
            />
          )}
                    <DeploymentsPanel projectId={projectId} />  
          <Board key={projectId} projectId={projectId} members={members} isLead={isLead} onError={setError} />
        </>
      ) : (
        <p className="empty">Create a project to begin. You become its lead.</p>
      )}
    </div>
  );
}