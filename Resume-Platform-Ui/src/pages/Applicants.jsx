import { Fragment, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getJson, openResume } from "../api";

export default function Applicants() {
  const { id } = useParams();
  const [job, setJob] = useState(null);
  const [apps, setApps] = useState([]);
  const [openId, setOpenId] = useState(null); // which row's AI explanation is expanded
  const [error, setError] = useState("");

  useEffect(() => {
    getJson(`/jobs/${id}`).then(setJob).catch((e) => setError(e.message));
    getJson(`/jobs/${id}/applications`).then(setApps).catch((e) => setError(e.message));
  }, [id]);

  return (
    <div className="page">
      <div className="title-row">
        <h1>Applicants{job ? `: ${job.title}` : ""}</h1>
        <Link to="/my-jobs">Back to my jobs</Link>
      </div>
      {error && <p className="error">{error}</p>}
      <div className="card">
        {apps.length === 0 ? (
          <p className="muted">No applications yet.</p>
        ) : (
          <table>
            <thead>
              <tr><th>Candidate</th><th>Email</th><th>Score</th><th>Status</th><th>Resume</th><th></th></tr>
            </thead>
            <tbody>
              {apps.map((a) => (
                <Fragment key={a.id}>
                  <tr>
                    <td>{a.candidateName}</td>
                    <td>{a.candidateEmail}</td>
                    <td>{a.matchScore != null ? `${a.matchScore} / 100` : "-"}</td>
                    <td><span className={`badge ${a.status}`}>{a.status}</span></td>
                    <td><button className="link-btn" onClick={() => openResume(a.id)}>View PDF</button></td>
                    <td>
                      {a.aiExplanation && (
                        <button className="link-btn" onClick={() => setOpenId(openId === a.id ? null : a.id)}>
                          {openId === a.id ? "Hide" : "AI notes"}
                        </button>
                      )}
                    </td>
                  </tr>
                  {openId === a.id && (
                    <tr><td colSpan="6" className="explain">{a.aiExplanation}</td></tr>
                  )}
                </Fragment>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
