import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { api, getJson } from "../api";

// Used for both "Post a job" (no id in the URL) and "Edit job" (id in the URL)
export default function JobForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({ title: "", description: "", skills: "", status: "OPEN" });
  const [error, setError] = useState("");

  useEffect(() => {
    if (!id) return;
    getJson(`/jobs/${id}`)
      .then((job) => setForm({ title: job.title, description: job.description, skills: job.requiredSkills.join(", "), status: job.status }))
      .catch((e) => setError(e.message));
  }, [id]);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    const body = {
      title: form.title,
      description: form.description,
      requiredSkills: form.skills.split(",").map((s) => s.trim()).filter(Boolean),
      status: form.status,
    };
    try {
      await api(id ? `/jobs/${id}` : "/jobs", { method: id ? "PUT" : "POST", body });
      navigate("/my-jobs");
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="page narrow">
      <h1>{id ? "Edit job" : "Post a job"}</h1>
      <form className="card" onSubmit={handleSubmit}>
        {error && <p className="error">{error}</p>}
        <label>Title</label>
        <input name="title" value={form.title} onChange={handleChange} required />
        <label>Description</label>
        <textarea name="description" rows="8" value={form.description} onChange={handleChange} required />
        <label>Required skills (separated by commas)</label>
        <input name="skills" value={form.skills} onChange={handleChange} placeholder="Java, Spring Boot, MySQL" />
        <label>Status</label>
        <select name="status" value={form.status} onChange={handleChange}>
          <option value="OPEN">Open</option>
          <option value="CLOSED">Closed</option>
        </select>
        <button className="btn">{id ? "Save changes" : "Post job"}</button>
      </form>
    </div>
  );
}
