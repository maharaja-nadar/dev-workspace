import { useState } from "react";
import { login, register, saveSession } from "../api";

export default function AuthPage({ onLoggedIn }) {
  const [mode, setMode] = useState("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [githubUsername, setGithubUsername] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      const data =
        mode === "login"
          ? await login({ username, password })
          : await register({ username, password, githubUsername: githubUsername || null });
      saveSession(data);
      onLoggedIn();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  function switchMode() {
    setMode(mode === "login" ? "register" : "login");
    setError("");
  }

  return (
    <main className="auth-card">
      <h1>DevWorkspace</h1>
      <h2>{mode === "login" ? "Log in" : "Create an account"}</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Username
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            minLength={3}
            autoComplete="username"
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={mode === "register" ? 8 : 1}
            autoComplete={mode === "login" ? "current-password" : "new-password"}
          />
        </label>
        {mode === "register" && (
          <label>
            GitHub username (optional)
            <input value={githubUsername} onChange={(e) => setGithubUsername(e.target.value)} />
          </label>
        )}
        {error && (
          <p className="error" role="alert">
            {error}
          </p>
        )}
        <button className="btn primary" disabled={busy}>
          {busy ? "Please wait..." : mode === "login" ? "Log in" : "Register"}
        </button>
      </form>
      <p>
        {mode === "login" ? "No account yet? " : "Already registered? "}
        <button type="button" className="link-btn" onClick={switchMode}>
          {mode === "login" ? "Create one" : "Log in"}
        </button>
      </p>
    </main>
  );
}