import { useEffect, useState } from "react";
import { getTasks, getEvents, createTask, updateStatus, deleteTask } from "../api";
import TaskCard from "./TaskCard";

const COLUMNS = [
  { status: "TODO", label: "To Do", color: "#94a3b8" },
  { status: "IN_PROGRESS", label: "In Progress", color: "#f59e0b" },
  { status: "IN_REVIEW", label: "In Review", color: "#8b5cf6" },
  { status: "DONE", label: "Done", color: "#10b981" },
];

export default function Board({ projectId, members, isLead, onError }) {
  const [tasks, setTasks] = useState([]);
  const [events, setEvents] = useState([]);
  const [title, setTitle] = useState("");
  const [assignee, setAssignee] = useState("");

  async function load(showErrors = true) {
    try {
      const [taskList, eventList] = await Promise.all([getTasks(projectId), getEvents(projectId)]);
      setTasks(taskList);
      setEvents(eventList);
      if (showErrors) onError("");
    } catch (e) {
      if (showErrors) onError(e.message);
    }
  }

  useEffect(() => {
    load();
    const timer = setInterval(() => load(false), 5000);
    return () => clearInterval(timer);
  }, [projectId]);

  async function handleAdd(e) {
    e.preventDefault();
    try {
      await createTask(projectId, { title, assignee: assignee || null });
      setTitle("");
      setAssignee("");
      await load();
    } catch (err) {
      onError(err.message);
    }
  }

  async function handleStatus(taskId, status) {
    try {
      await updateStatus(taskId, status);
      await load();
    } catch (err) {
      onError(err.message);
    }
  }

  async function handleDelete(taskId) {
    try {
      await deleteTask(taskId);
      await load();
    } catch (err) {
      onError(err.message);
    }
  }

  const total = tasks.length;
  const done = tasks.filter((t) => t.status === "DONE").length;
  const percent = total === 0 ? 0 : Math.round((done / total) * 100);

  return (
    <>
      <div className="stats">
        {COLUMNS.map((col) => (
          <div className="stat" key={col.status}>
            <div className="stat-top">
              <span className="stat-dot" style={{ background: col.color }} />
              {col.label}
            </div>
            <div className="stat-num">{tasks.filter((t) => t.status === col.status).length}</div>
          </div>
        ))}
      </div>

      <div className="progress-wrap">
        <div className="progress-meta">
          <span>Overall progress</span>
          <span>
            {done} of {total} done ({percent}%)
          </span>
        </div>
        <div className="progress">
          <div className="progress-fill" style={{ width: percent + "%" }} />
        </div>
      </div>

      {isLead ? (
        <form className="new-task inline-form" onSubmit={handleAdd}>
          <input
            style={{ flex: 1, minWidth: 200 }}
            placeholder="New task title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
          <select value={assignee} onChange={(e) => setAssignee(e.target.value)} aria-label="Assignee">
            <option value="">Unassigned</option>
            {members.map((m) => (
              <option key={m.id} value={m.username}>
                {m.username}
              </option>
            ))}
          </select>
          <button className="btn primary">Add task</button>
        </form>
      ) : (
        <p className="hint">Your lead creates and assigns tasks. You can update the status of the work you do.</p>
      )}

      <div className="columns">
        {COLUMNS.map((col) => {
          const items = tasks.filter((t) => t.status === col.status);
          return (
            <section className="column" key={col.status}>
              <div className="col-head">
                <span className="col-dot" style={{ background: col.color }} />
                {col.label}
                <span className="count">{items.length}</span>
              </div>
              {items.length === 0 && <div className="col-empty">No tasks</div>}
              {items.map((t) => (
                <TaskCard
                  key={t.id}
                  task={t}
                  events={events.filter((ev) => ev.taskKey === t.taskKey)}
                  canDelete={isLead}
                  onStatusChange={handleStatus}
                  onDelete={handleDelete}
                />
              ))}
            </section>
          );
        })}
      </div>
    </>
  );
}