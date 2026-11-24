import { avatarColor, initials } from "../avatar";

const STATUSES = [
  ["TODO", "To Do"],
  ["IN_PROGRESS", "In Progress"],
  ["IN_REVIEW", "In Review"],
  ["DONE", "Done"],
];

const EVENT_LABELS = {
  COMMIT: ["Commit", "tag"],
  PR_OPENED: ["PR opened", "tag pr"],
  PR_MERGED: ["PR merged", "tag merged"],
  PR_CLOSED: ["PR closed", "tag pr"],
};

export default function TaskCard({ task, events, canDelete, onStatusChange, onDelete }) {
  return (
    <div className="card">
      <div className="card-top">
        <span className="key">{task.taskKey}</span>
        {canDelete && (
          <button className="link" onClick={() => onDelete(task.id)}>
            Delete
          </button>
        )}
      </div>
      <div className="title">{task.title}</div>
      <div className="meta">
        {task.assignee ? (
          <span className="assignee">
            <span className="avatar sm" style={{ background: avatarColor(task.assignee) }}>
              {initials(task.assignee)}
            </span>
            {task.assignee}
          </span>
        ) : (
          <span className="assignee muted">Unassigned</span>
        )}
        <select
          className="status-select"
          value={task.status}
          onChange={(e) => onStatusChange(task.id, e.target.value)}
          aria-label="Change status"
        >
          {STATUSES.map(([value, label]) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
      </div>

      {events.length > 0 && (
        <details className="activity">
          <summary>{events.length} GitHub activity</summary>
          <ul>
            {events.map((ev) => {
              const [label, css] = EVENT_LABELS[ev.type] || [ev.type, "tag"];
              return (
                <li key={ev.id}>
                  <span className={css}>{label}</span>
                  {ev.message}
                  {ev.author && <span className="by"> by {ev.author}</span>}
                  {ev.url && ev.url.startsWith("https://") && (
                    <a href={ev.url} target="_blank" rel="noreferrer">
                      view
                    </a>
                  )}
                </li>
              );
            })}
          </ul>
        </details>
      )}
    </div>
  );
}