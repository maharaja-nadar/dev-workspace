import { useState } from "react";
import { login, register, saveSession } from "../api";

export default function AuthPage({ onLoggedIn }) {
  const [mode, setMode] = useState("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [githubUsername, setGithubUsername] = useState("");
  const [teamLead, setTeamLead] = useState(false);
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
          : await register({ username, password, githubUsername: githubUsername || null, teamLead });
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
    <div className="auth-wrap">
      <aside className="auth-side">
        <div className="brand brand-light">
          <span className="logo">DV</span>
          <span className="brand-name">Dev Vectra</span>
        </div>
        <div>
          <h2 className="auth-tagline">Tasks that follow your code.</h2>
          <p className="auth-sub">
            Plan the work, push to GitHub, and watch every task move on its own.
          </p>
          <ul className="auth-points">
            <li>Leads assign, developers deliver</li>
            <li>Commits and pull requests update the board</li>
            <li>Every deployment is recorded</li>
          </ul>
        </div>
        <p className="auth-foot">Built with React, Spring Boot and AWS</p>
      </aside>

      <main className="auth-main">
        <div className="auth-card">
          <h2>{mode === "login" ? "Welcome back" : "Create your account"}</h2>
          <p className="sub">
            {mode === "login" ? "Log in to your workspace." : "It takes less than a minute."}
          </p>
          <form onSubmit={handleSubmit}>
            <label className="field">
              Username
              <input
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                minLength={3}
                autoComplete="username"
              />
            </label>
            <label className="field">
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
              <>
                <label className="field">
                  GitHub username (optional)
                  <input value={githubUsername} onChange={(e) => setGithubUsername(e.target.value)} />
                </label>
                <div className="field">
                  I am a
                  <div className="seg">
                    <button type="button" className={!teamLead ? "active" : ""} onClick={() => setTeamLead(false)}>
                      Developer
                    </button>
                    <button type="button" className={teamLead ? "active" : ""} onClick={() => setTeamLead(true)}>
                      Team lead
                    </button>
                  </div>
                </div>
              </>
            )}
            {error && (
              <p className="error" role="alert" style={{ margin: 0 }}>
                {error}
              </p>
            )}
            <button className="btn primary" disabled={busy}>
              {busy ? "Please wait..." : mode === "login" ? "Log in" : "Create account"}
            </button>
          </form>
          <p className="switch">
            {mode === "login" ? "New here? " : "Already registered? "}
            <button type="button" className="link-btn" onClick={switchMode}>
              {mode === "login" ? "Create an account" : "Log in"}
            </button>
          </p>
        </div>
      </main>
    </div>
  );
}