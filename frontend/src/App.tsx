import { Navigate, Route, Routes } from 'react-router-dom';
import { DocumentDetails, Documents, Login } from './pages';
import { Main } from './pages/Main.tsx';
import './styles/app.scss';
import {Employes} from "./pages/Employes.tsx";

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/main" element={<Main />} />
      <Route path="/employes" element={<Employes />} />
      <Route path="/documents" element={<Documents />} />
      <Route path="/document" element={<DocumentDetails />} />
    </Routes>
  );
};
