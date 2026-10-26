import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api";

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ userName: "", userEmail: "", userPassword: "", role: "CANDIDATE" });
  const [error, setError] = useState("");

  // One handler for every input, using the input's name attribute
  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      await api("/register", { method: "POST", body: form });
      navigate("/login");
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="page narrow">
      <div className="card">
        <h2>Create an account</h2>
        {error && <p className="error">{error}</p>}
        <form onSubmit={handleSubmit}>
          <label>Username</label>
          <input name="userName" value={form.userName} onChange={handleChange} required />
          <label>Email</label>
          <input name="userEmail" type="email" value={form.userEmail} onChange={handleChange} required />
          <label>Password (8 characters or more)</label>
          <input name="userPassword" type="password" value={form.userPassword} onChange={handleChange} required />
          <label>I am a</label>
          <select name="role" value={form.role} onChange={handleChange}>
            <option value="CANDIDATE">Candidate</option>
            <option value="RECRUITER">Recruiter</option>
          </select>
          <button className="btn">Register</button>
        </form>
        <p className="muted">Already registered? <Link to="/login">Log in</Link></p>
      </div>
    </div>
  );
}
