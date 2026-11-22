import { useEffect, useState } from "react";
import { getDeployments } from "../api";

export default function DeploymentsPanel({ projectId }) {
  const [items, setItems] = useState([]);

  useEffect(() => {
    let active = true;

    async function load() {
      try {
        const list = await getDeployments(projectId);
        if (active) setItems(list);
      } catch {
        // keep the previous list
      }
    }

    load();
    const timer = setInterval(load, 10000);
    return () => {
      active = false;
      clearInterval(timer);
    };
  }, [projectId]);

  return (
    <section className="members">
      <h2>Deployments</h2>
      {items.length === 0 ? (
        <p className="repo-line">No deployments recorded yet.</p>
      ) : (
        <ul className="member-list">
          {items.slice(0, 5).map((d) => (
            <li key={d.id} className="member">
              <span className={"badge" + (d.status === "SUCCESS" ? " lead" : "")}>{d.status}</span>
              <code>{d.commitSha.slice(0, 7)}</code>
              <span>{d.message}</span>
              <span className="by">{d.deployedAt.replace("T", " ").slice(0, 16)} UTC</span>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}