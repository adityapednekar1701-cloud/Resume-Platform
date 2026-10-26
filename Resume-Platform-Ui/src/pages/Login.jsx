import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [userName, setUserName] = useState("");
  const [userPassword, setUserPassword] = useState("");
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      await login(userName, userPassword);
      navigate("/");
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="page narrow">
      <div className="card">
        <h2>Log in</h2>
        {error && <p className="error">{error}</p>}
        <form onSubmit={handleSubmit}>
          <label>Username</label>
          <input value={userName} onChange={(e) => setUserName(e.target.value)} required />
          <label>Password</label>
          <input type="password" value={userPassword} onChange={(e) => setUserPassword(e.target.value)} required />
          <button className="btn">Log in</button>
        </form>
        <p className="muted">No account? <Link to="/register">Register</Link></p>
      </div>
    </div>
  );
}
