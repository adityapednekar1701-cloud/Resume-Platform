import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getJson } from "../api";

export default function Jobs() {
  const [jobs, setJobs] = useState([]);
  const [status, setStatus] = useState("OPEN");
  const [error, setError] = useState("");

  // Runs on first render and again whenever the filter changes
  useEffect(() => {
    const query = status ? `?status=${status}` : "";
    getJson("/jobs" + query).then(setJobs).catch((e) => setError(e.message));
  }, [status]);

  return (
    <div className="page">
      <div className="title-row">
        <h1>Jobs</h1>
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="OPEN">Open</option>
          <option value="CLOSED">Closed</option>
          <option value="">All</option>
        </select>
      </div>
      {error && <p className="error">{error}</p>}
      {jobs.length === 0 && !error && <p className="muted">No jobs found.</p>}
      {jobs.map((job) => (
        <div className="card" key={job.id}>
          <div className="title-row">
            <Link to={`/jobs/${job.id}`}><h3>{job.title}</h3></Link>
            <span className={`badge ${job.status}`}>{job.status}</span>
          </div>
          <p className="muted">Posted by {job.recruiterName} on {new Date(job.createdAt).toLocaleDateString()}</p>
          <div>{job.requiredSkills.map((s) => <span className="chip" key={s}>{s}</span>)}</div>
        </div>
      ))}
    </div>
  );
}
