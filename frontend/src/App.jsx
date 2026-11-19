import { useState } from "react";
import { getToken, clearSession } from "./api";
import AuthPage from "./components/AuthPage";
import Workspace from "./components/Workspace";
import "./App.css";

export default function App() {
  const [loggedIn, setLoggedIn] = useState(Boolean(getToken()));

  function handleLogout() {
    clearSession();
    setLoggedIn(false);
  }

  if (!loggedIn) {
    return <AuthPage onLoggedIn={() => setLoggedIn(true)} />;
  }
  return <Workspace onLogout={handleLogout} />;
}