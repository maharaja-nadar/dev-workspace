import { useEffect, useState } from "react";
import { getProjects, createProject, getMembers, getUsername, getCanCreate } from "../api";
import { avatarColor, initials } from "../avatar";
import Board from "./Board";
import MembersPanel from "./MembersPanel";
import RepoPanel from "./RepoPanel";

export default function Workspace({ onLogout }) {
  const me = getUsername();
  const [projects, setProjects] = useState([]);
  const [projectId, setProjectId] = useState(null);
  const [members, setMembers] = useState([]);
  const [canCreate, setCanCreate] = useState(false);
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
    getCanCreate()
      .then((r) => setCanCreate(r.canCreate))
      .catch(() => {});
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
        <div className="brand">
          <span className="logo">DV</span>
          <span className="brand-name">Dev Vectra</span>
        </div>
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
          <div className="chip">
            <span className="avatar" style={{ background: avatarColor(me) }}>
              {initials(me)}
            </span>
            <span className="chip-name">{me}</span>
            {project && (
              <span className={"badge" + (isLead ? " lead" : "")}>{isLead ? "Lead" : "Developer"}</span>
            )}
          </div>
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

      {canCreate && (
        <details className="new-project">
          <summary>+ New project</summary>
          <form className="inline-form" onSubmit={handleCreateProject}>
            <input placeholder="Project name" value={name} onChange={(e) => setName(e.target.value)} required />
            <input
              placeholder="Key (e.g. WS)"
              value={keyPrefix}
              onChange={(e) => setKeyPrefix(e.target.value)}
              maxLength={5}
              required
            />
            <button className="btn primary">Create project</button>
          </form>
        </details>
      )}

      {projectId && project ? (
        <>
          <div className="page-head">
            <h1>{project.name}</h1>
            <span className="key-pill">{project.keyPrefix}</span>
          </div>
          <div className="panels" style={{ marginBottom: 14 }}>
            <MembersPanel
              projectId={projectId}
              members={members}
              isLead={isLead}
              onChanged={loadMembers}
              onError={setError}
            />
            <RepoPanel
              project={project}
              isLead={isLead}
              onLinked={() => loadProjects(projectId)}
              onError={setError}
            />
          </div>
          <Board key={projectId} projectId={projectId} members={members} isLead={isLead} onError={setError} />
        </>
      ) : (
        <p className="empty">
          {canCreate
            ? "Create your first project to get started. You will be its lead."
            : "You are not in a project yet. Ask your team lead to add you."}
        </p>
      )}
    </div>
  );
}