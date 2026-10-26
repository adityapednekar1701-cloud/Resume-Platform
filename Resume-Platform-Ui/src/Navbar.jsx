import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <header className="navbar">
      <Link to="/" className="logo">HireScore</Link>
      <nav>
        <NavLink to="/" end>Jobs</NavLink>
        {user?.role === "CANDIDATE" && <NavLink to="/my-applications">My applications</NavLink>}
        {user?.role === "RECRUITER" && <NavLink to="/my-jobs">My jobs</NavLink>}
        {user?.role === "RECRUITER" && <NavLink to="/jobs/new">Post a job</NavLink>}
      </nav>
      <div className="nav-right">
        {user ? (
          <>
            <span className="avatar" title={user.userName}>{user.userName.slice(0, 2).toUpperCase()}</span>
            <button className="link-btn" onClick={handleLogout}>Log out</button>
          </>
        ) : (
          <>
            <NavLink to="/login">Log in</NavLink>
            <NavLink to="/register">Register</NavLink>
          </>
        )}
      </div>
    </header>
  );
}
