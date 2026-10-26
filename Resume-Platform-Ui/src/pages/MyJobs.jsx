import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, getJson } from "../api";
import { useAuth } from "../AuthContext";

export default function MyJobs() {
  const { user } = useAuth();
  const [jobs, setJobs] = useState([]);
  const [error, setError] = useState("");

  // The backend has no "my jobs" endpoint, so filter by recruiter name here
  function load() {
    getJson("/jobs")
      .then((all) => setJobs(all.filter((j) => j.recruiterName === user.userName)))
      .catch((e) => setError(e.message));
  }

  useEffect(load, []);

  async function toggleStatus(job) {
    try {
      await api(`/jobs/${job.id}`, { method: "PUT", body: { status: job.status === "OPEN" ? "CLOSED" : "OPEN" } });
      load();
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleDelete(job) {
    if (!window.confirm(`Delete "${job.title}"?`)) return;
    try {
      await api(`/jobs/${job.id}`, { method: "DELETE" });
      load();
    } catch (err) {
      setError(err.message); // e.g. job already has applications
    }
  }

  return (
    <div className="page">
      <div className="title-row">
        <h1>My jobs</h1>
        <Link className="btn" to="/jobs/new">Post a job</Link>
      </div>
      {error && <p className="error">{error}</p>}
      <div className="card">
        {jobs.length === 0 ? (
          <p className="muted">You have not posted any jobs yet.</p>
        ) : (
          <table>
            <thead>
              <tr><th>Title</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {jobs.map((job) => (
                <tr key={job.id}>
                  <td><Link to={`/jobs/${job.id}`}>{job.title}</Link></td>
                  <td><span className={`badge ${job.status}`}>{job.status}</span></td>
                  <td className="actions">
                    <Link to={`/jobs/${job.id}/applications`}>Applicants</Link>
                    <Link to={`/jobs/${job.id}/edit`}>Edit</Link>
                    <button className="link-btn" onClick={() => toggleStatus(job)}>{job.status === "OPEN" ? "Close" : "Reopen"}</button>
                    <button className="link-btn danger" onClick={() => handleDelete(job)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
