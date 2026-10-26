// Backend URL. CORS is enabled on the Spring Boot side for http://localhost:5173
const BASE = "http://localhost:8080";

// One place that adds the JWT header and turns error responses into Error objects.
export async function api(path, { method = "GET", body, form } = {}) {
  const headers = {};
  const token = localStorage.getItem("token");
  if (token) headers.Authorization = `Bearer ${token}`;

  let payload;
  if (form) {
    payload = form; // browser sets the multipart boundary itself
  } else if (body) {
    headers["Content-Type"] = "application/json";
    payload = JSON.stringify(body);
  }

  const res = await fetch(BASE + path, { method, headers, body: payload });

  if (res.status === 401 && token) {
    // token expired (1 hour) -> back to login
    localStorage.removeItem("token");
    window.location.href = "/login";
  }
  if (!res.ok) throw new Error(await readError(res));
  return res;
}

export async function getJson(path, options) {
  return (await api(path, options)).json();
}

// Backend sends ProblemDetail JSON: { detail, errors: { field: message } }
async function readError(res) {
  try {
    const p = await res.json();
    if (p.errors) return Object.values(p.errors).join(", ");
    return p.detail || "Request failed";
  } catch {
    return `Request failed (${res.status})`;
  }
}

// Resume needs the Authorization header, so a plain <a href> would not work.
export async function openResume(applicationId) {
  const res = await api(`/applications/${applicationId}/resume`);
  const blob = await res.blob();
  window.open(URL.createObjectURL(blob), "_blank");
}
