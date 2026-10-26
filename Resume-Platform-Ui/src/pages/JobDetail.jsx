import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { api, getJson } from "../api";
import { useAuth } from "../AuthContext";

export default function JobDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [file, setFile] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    getJson(`/jobs/${id}`).then(setJob).catch((e) => setError(e.message));
  }, [id]);

  async function handleApply(e) {
    e.preventDefault();
    setError("");
    if (!file) return setError("Choose a PDF resume");
    if (file.type !== "application/pdf") return setError("Resume must be a PDF");
    if (file.size > 5 * 1024 * 1024) return setError("Resume must be 5MB or smaller");

    // Backend expects two parts: "request" (JSON) and "resumeFile"
    const form = new FormData();
    form.append("request", new Blob([JSON.stringify({ jobId: job.id })], { type: "application/json" }));
    form.append("resumeFile", file);
    try {
      await api("/applications", { method: "POST", form });
      navigate("/my-applications");
    } catch (err) {
      setError(err.message);
    }
  }

  if (!job) return <div className="page">{error ? <p className="error">{error}</p> : "Loading..."}</div>;

  return (
    <div className="page">
      <div className="title-row">
        <h1>{job.title}</h1>
        <span className={`badge ${job.status}`}>{job.status}</span>
      </div>
      <div className="card">
        <p className="muted">Posted by {job.recruiterName}</p>
        <p className="pre">{job.description}</p>
        <div>{job.requiredSkills.map((s) => <span className="chip" key={s}>{s}</span>)}</div>
      </div>

      {error && <p className="error">{error}</p>}

      {!user && <p className="muted"><Link to="/login">Log in</Link> as a candidate to apply.</p>}

      {user?.role === "CANDIDATE" && job.status === "OPEN" && (
        <form className="card" onSubmit={handleApply}>
          <h3>Apply for this job</h3>
          <label>Resume (PDF, max 5MB)</label>
          <input type="file" accept="application/pdf" onChange={(e) => setFile(e.target.files[0])} />
          <button className="btn">Submit application</button>
        </form>
      )}
      {user?.role === "CANDIDATE" && job.status === "CLOSED" && <p className="muted">This job is closed.</p>}

      {user?.role === "RECRUITER" && user.userName === job.recruiterName && (
        <Link className="btn" to={`/jobs/${job.id}/applications`}>View applicants</Link>
      )}
    </div>
  );
}
