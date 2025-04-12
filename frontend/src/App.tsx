import { Navigate, Route, Routes } from 'react-router-dom';
import { DocumentDetails, Documents, Login } from './pages';
import { Main } from './pages/Main.tsx';
import './styles/app.scss';
import {Employes} from "./pages/Employes.tsx";
import {Departments} from "./pages/Departments.tsx";

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/main" element={<Main />} />
      <Route path="/employees" element={<Employes />} />
      <Route path="/departments" element={<Departments />} />
      <Route path="/documents" element={<Documents />} />
      <Route path="/document" element={<DocumentDetails />} />
    </Routes>
  );
};
