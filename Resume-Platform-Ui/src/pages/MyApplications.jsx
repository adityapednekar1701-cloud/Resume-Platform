import { useEffect, useState } from "react";
import { getJson, openResume } from "../api";

export default function MyApplications() {
  const [apps, setApps] = useState([]);
  const [error, setError] = useState("");

  function load() {
    getJson("/applications/me").then(setApps).catch((e) => setError(e.message));
  }

  useEffect(load, []);

  // Scoring happens in the background (Kafka), so keep refreshing while anything is still pending
  const pending = apps.some((a) => a.status === "SUBMITTED" || a.status === "PROCESSING");
  useEffect(() => {
    if (!pending) return;
    const timer = setInterval(load, 4000);
    return () => clearInterval(timer); // cleanup when pending ends or page closes
  }, [pending]);

  return (
    <div className="page">
      <h1>My applications</h1>
      {error && <p className="error">{error}</p>}
      <div className="card">
        {apps.length === 0 ? (
          <p className="muted">You have not applied to any jobs yet.</p>
        ) : (
          <table>
            <thead>
              <tr><th>Job</th><th>Applied</th><th>Resume</th><th>Status</th></tr>
            </thead>
            <tbody>
              {apps.map((a) => (
                <tr key={a.id}>
                  <td>{a.jobTitle}</td>
                  <td>{new Date(a.appliedAt).toLocaleDateString()}</td>
                  <td><button className="link-btn" onClick={() => openResume(a.id)}>{a.resumeName}</button></td>
                  <td><span className={`badge ${a.status}`}>{a.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
