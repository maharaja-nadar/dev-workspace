import { useEffect, useState } from "react";
import { getTasks, createTask, updateStatus, deleteTask } from "../api";
import TaskCard from "./TaskCard";

const COLUMNS = [
  { status: "TODO", label: "To Do" },
  { status: "IN_PROGRESS", label: "In Progress" },
  { status: "IN_REVIEW", label: "In Review" },
  { status: "DONE", label: "Done" },
];

export default function Board({ projectId, members, isLead, onError }) {
  const [tasks, setTasks] = useState([]);
  const [title, setTitle] = useState("");
  const [assignee, setAssignee] = useState("");

  async function load() {
    try {
      setTasks(await getTasks(projectId));
      onError("");
    } catch (e) {
      onError(e.message);
    }
  }

  useEffect(() => {
    load();
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

  return (
    <>
      <form className="inline-form" onSubmit={handleAdd}>
        <input
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

      <div className="columns">
        {COLUMNS.map((col) => {
          const items = tasks.filter((t) => t.status === col.status);
          return (
            <section className="column" key={col.status}>
              <h2>
                {col.label} <span className="count">{items.length}</span>
              </h2>
              {items.map((t) => (
                <TaskCard
                  key={t.id}
                  task={t}
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