import { Navigate, Route, Routes } from 'react-router-dom';
import { DocumentDetails, Documents, Login } from './pages';
import { Departments } from './pages/Departments.tsx';
import { Employes } from './pages/Employes.tsx';
import { Main } from './pages/Main.tsx';
import './styles/app.scss';

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/main" element={<Main />} />
      <Route path="/employees" element={<Employes />} />
      <Route path="/departments" element={<Departments />} />
      
      <Route path="/documents/:orgId" element={<Documents />} />
      <Route path="/document/:docId" element={<DocumentDetails />} />
    </Routes>
  );
};