import { useState } from "react";
import { addMember, removeMember } from "../api";

export default function MembersPanel({ projectId, members, isLead, onChanged, onError }) {
  const [username, setUsername] = useState("");
  const [role, setRole] = useState("DEVELOPER");

  async function handleAdd(e) {
    e.preventDefault();
    try {
      await addMember(projectId, { username: username.trim(), role });
      setUsername("");
      onError("");
      await onChanged();
    } catch (err) {
      onError(err.message);
    }
  }

  async function handleRemove(name) {
    try {
      await removeMember(projectId, name);
      onError("");
      await onChanged();
    } catch (err) {
      onError(err.message);
    }
  }

  return (
    <section className="members">
      <h2>Members</h2>
      <ul className="member-list">
        {members.map((m) => (
          <li key={m.id} className="member">
            <span className="avatar">{m.username.slice(0, 2).toUpperCase()}</span>
            <span>{m.username}</span>
            <span className={"badge" + (m.role === "LEAD" ? " lead" : "")}>{m.role}</span>
            {isLead && m.role !== "LEAD" && (
              <button className="link" onClick={() => handleRemove(m.username)}>
                Remove
              </button>
            )}
          </li>
        ))}
      </ul>
      {isLead && (
        <form className="inline-form" onSubmit={handleAdd}>
          <input
            placeholder="Username to add"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <select value={role} onChange={(e) => setRole(e.target.value)} aria-label="Role">
            <option value="DEVELOPER">Developer</option>
            <option value="LEAD">Lead</option>
          </select>
          <button className="btn">Add member</button>
        </form>
      )}
    </section>
  );
}