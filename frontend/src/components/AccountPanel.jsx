import { useEffect, useState } from "react";
import { getProfile, updateGithubUsername, changePassword } from "../api";

export default function AccountPanel({ onError }) {
  const [github, setGithub] = useState("");
  const [current, setCurrent] = useState("");
  const [next, setNext] = useState("");
  const [confirm, setConfirm] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    getProfile()
      .then((p) => setGithub(p.githubUsername || ""))
      .catch((e) => onError(e.message));
  }, []);

  async function saveGithub(value) {
    try {
      const p = await updateGithubUsername(value.trim());
      setGithub(p.githubUsername || "");
      setMessage(p.githubUsername ? "GitHub username saved." : "GitHub username removed.");
      onError("");
    } catch (err) {
      onError(err.message);
    }
  }

  async function handlePassword(e) {
    e.preventDefault();
    if (next !== confirm) {
      onError("The new passwords do not match.");
      return;
    }
    try {
      await changePassword(current, next);
      setCurrent("");
      setNext("");
      setConfirm("");
      setMessage("Password changed.");
      onError("");
    } catch (err) {
      onError(err.message);
    }
  }

  return (
    <details className="members">
      <summary>My account</summary>

      <h2>GitHub username (optional)</h2>
      <form
        className="inline-form"
        onSubmit={(e) => {
          e.preventDefault();
          saveGithub(github);
        }}
      >
        <input placeholder="your GitHub username" value={github} onChange={(e) => setGithub(e.target.value)} />
        <button className="btn">Save</button>
        <button type="button" className="btn" onClick={() => saveGithub("")}>
          Remove
        </button>
      </form>

      <h2>Change password</h2>
      <form className="inline-form" onSubmit={handlePassword}>
        <input type="password" placeholder="Current password" value={current}
               onChange={(e) => setCurrent(e.target.value)} required autoComplete="current-password" />
        <input type="password" placeholder="New password (8+ characters)" value={next}
               onChange={(e) => setNext(e.target.value)} required minLength={8} autoComplete="new-password" />
        <input type="password" placeholder="Repeat new password" value={confirm}
               onChange={(e) => setConfirm(e.target.value)} required minLength={8} autoComplete="new-password" />
        <button className="btn primary">Change password</button>
      </form>

      {message && <p className="repo-line" role="status">{message}</p>}
    </details>
  );
}