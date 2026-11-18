import { useEffect, useState } from "react";
import { getProjects, createProject } from "./api";
import Board from "./components/Board";
import "./App.css";

export default function App() {
  const [projects, setProjects] = useState([]);
  const [projectId, setProjectId] = useState(null);
  const [error, setError] = useState("");
  const [name, setName] = useState("");
  const [keyPrefix, setKeyPrefix] = useState("");

  async function loadProjects() {
    try {
      const list = await getProjects();
      setProjects(list);
      if (list.length > 0 && projectId === null) setProjectId(list[0].id);
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    loadProjects();
  }, []);

  async function handleCreateProject(e) {
    e.preventDefault();
    setError("");
    try {
      const created = await createProject({
        name,
        keyPrefix: keyPrefix.toUpperCase(),
      });
      setName("");
      setKeyPrefix("");
      await loadProjects();
      setProjectId(created.id);
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="app">
      <header className="top">
        <h1>DevWorkspace</h1>
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
      </header>

      {error && <p className="error">{error}</p>}

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
        <Board projectId={projectId} onError={setError} />
      ) : (
        <p className="empty">Create a project to begin.</p>
      )}
    </div>
  );
}