import { useState } from "react";
import { linkRepo } from "../api";

export default function RepoPanel({ project, isLead, onLinked, onError }) {
  const [repo, setRepo] = useState("");

  async function handleLink(e) {
    e.preventDefault();
    try {
      await linkRepo(project.id, repo.trim());
      setRepo("");
      onError("");
      await onLinked();
    } catch (err) {
      onError(err.message);
    }
  }

  return (
    <section className="members">
      <h2>GitHub repository</h2>
      <p className="repo-line">
        {project.repoFullName ? (
          <>
            <span className="badge lead">Linked</span> {project.repoFullName}
          </>
        ) : (
          "Not linked yet"
        )}
      </p>
      {isLead && (
        <form className="inline-form" onSubmit={handleLink}>
          <input
            placeholder="owner/repository"
            value={repo}
            onChange={(e) => setRepo(e.target.value)}
            required
          />
          <button className="btn">{project.repoFullName ? "Change repo" : "Link repo"}</button>
        </form>
      )}
    </section>
  );
}