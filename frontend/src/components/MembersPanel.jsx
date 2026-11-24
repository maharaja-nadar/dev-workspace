import { useEffect, useState } from "react";
import { addMember, removeMember, getMemberCandidates } from "../api";
import { avatarColor, initials } from "../avatar";

export default function MembersPanel({ projectId, members, isLead, onChanged, onError }) {
  const [query, setQuery] = useState("");
  const [role, setRole] = useState("DEVELOPER");
  const [options, setOptions] = useState([]);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    if (!isLead || !open) return;
    const timer = setTimeout(async () => {
      try {
        setOptions(await getMemberCandidates(projectId, query));
      } catch {
        setOptions([]);
      }
    }, 150);
    return () => clearTimeout(timer);
  }, [query, open, projectId, isLead, members.length]);

  async function handleAdd(e) {
    e.preventDefault();
    try {
      await addMember(projectId, { username: query.trim(), role });
      setQuery("");
      setOpen(false);
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
            <span className="avatar" style={{ background: avatarColor(m.username) }}>
              {initials(m.username)}
            </span>
            <span>{m.username}</span>
            <span className={"badge" + (m.role === "LEAD" ? " lead" : "")}>
              {m.role === "LEAD" ? "Lead" : "Developer"}
            </span>
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
          <div className="combo">
            <input
              placeholder="Search a username, or pick from the list"
              value={query}
              onChange={(e) => {
                setQuery(e.target.value);
                setOpen(true);
              }}
              onFocus={() => setOpen(true)}
              onBlur={() => setOpen(false)}
              autoComplete="off"
              required
            />
            {open && (
              <ul className="combo-list">
                {options.length === 0 && <li className="combo-empty">No matching users</li>}
                {options.map((name) => (
                  <li
                    key={name}
                    className="combo-item"
                    onMouseDown={(e) => {
                      e.preventDefault();
                      setQuery(name);
                      setOpen(false);
                    }}
                  >
                    <span className="avatar sm" style={{ background: avatarColor(name) }}>
                      {initials(name)}
                    </span>
                    {name}
                  </li>
                ))}
              </ul>
            )}
          </div>
          <select value={role} onChange={(e) => setRole(e.target.value)} aria-label="Role">
            <option value="DEVELOPER">Developer</option>
            <option value="LEAD">Lead</option>
          </select>
          <button className="btn primary">Add member</button>
        </form>
      )}
    </section>
  );
}