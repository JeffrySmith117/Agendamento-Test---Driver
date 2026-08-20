import { Navigate, Route, BrowserRouter, Routes } from "react-router-dom";
import Login from "./pages/Login";
import AgendarPage from "./pages/AgendarPage";
import AdminDashboard from "./pages/AdminDashboard";

function RotaProtegida({ children, exigeAdmin = false }: { children: JSX.Element; exigeAdmin?: boolean }) {
  const token = localStorage.getItem("token");
  const perfil = localStorage.getItem("perfil");

  if (!token) return <Navigate to="/login" replace />;
  if (exigeAdmin && perfil !== "ADMIN") return <Navigate to="/agendar" replace />;

  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route
          path="/agendar"
          element={
            <RotaProtegida>
              <AgendarPage />
            </RotaProtegida>
          }
        />
        <Route
          path="/admin"
          element={
            <RotaProtegida exigeAdmin>
              <AdminDashboard />
            </RotaProtegida>
          }
        />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
