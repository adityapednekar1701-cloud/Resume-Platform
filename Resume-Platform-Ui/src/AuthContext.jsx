import { createContext, useContext, useEffect, useState } from "react";
import { api, getJson } from "./api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null); // { userName, userEmail, role }
  const [loading, setLoading] = useState(!!localStorage.getItem("token"));

  // On page refresh, restore the user from the saved token
  useEffect(() => {
    if (!localStorage.getItem("token")) return;
    getJson("/users/me")
      .then(setUser)
      .catch(() => localStorage.removeItem("token"))
      .finally(() => setLoading(false));
  }, []);

  async function login(userName, userPassword) {
    const res = await api("/login", { method: "POST", body: { userName, userPassword } });
    localStorage.setItem("token", await res.text()); // login returns plain text, not JSON
    setUser(await getJson("/users/me"));
  }

  function logout() {
    localStorage.removeItem("token");
    setUser(null);
  }

  return <AuthContext.Provider value={{ user, loading, login, logout }}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
