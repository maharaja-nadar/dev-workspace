const STATUSES = [
  ["TODO", "To Do"],
  ["IN_PROGRESS", "In Progress"],
  ["IN_REVIEW", "In Review"],
  ["DONE", "Done"],
];

export default function TaskCard({ task, canDelete, onStatusChange, onDelete }) {
  return (
    <div className="card">
      <span className="key">{task.taskKey}</span>
      <div className="title">{task.title}</div>
      <div className="meta">
        {task.assignee && (
          <span className="avatar" title={task.assignee}>
            {task.assignee.slice(0, 2).toUpperCase()}
          </span>
        )}
        <select
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
        {canDelete && (
          <button className="link" onClick={() => onDelete(task.id)}>
            Delete
          </button>
        )}
      </div>
    </div>
  );
}