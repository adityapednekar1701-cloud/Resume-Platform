import { Navigate, Route, Routes } from "react-router-dom";
import { useAuth } from "./AuthContext";
import Navbar from "./Navbar";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Jobs from "./pages/Jobs";
import JobDetail from "./pages/JobDetail";
import MyApplications from "./pages/MyApplications";
import MyJobs from "./pages/MyJobs";
import JobForm from "./pages/JobForm";
import Applicants from "./pages/Applicants";

// Only lets the right role see a page
function RequireRole({ role, children }) {
  const { user, loading } = useAuth();
  if (loading) return <p className="page">Loading...</p>;
  if (!user) return <Navigate to="/login" />;
  if (role && user.role !== role) return <Navigate to="/" />;
  return children;
}

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Jobs />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/jobs/:id" element={<JobDetail />} />
        <Route path="/my-applications" element={<RequireRole role="CANDIDATE"><MyApplications /></RequireRole>} />
        <Route path="/my-jobs" element={<RequireRole role="RECRUITER"><MyJobs /></RequireRole>} />
        <Route path="/jobs/new" element={<RequireRole role="RECRUITER"><JobForm /></RequireRole>} />
        <Route path="/jobs/:id/edit" element={<RequireRole role="RECRUITER"><JobForm /></RequireRole>} />
        <Route path="/jobs/:id/applications" element={<RequireRole role="RECRUITER"><Applicants /></RequireRole>} />
      </Routes>
    </>
  );
}
