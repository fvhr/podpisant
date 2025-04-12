import { Navigate, Route, Routes } from 'react-router-dom';
import { Auth, Organization } from './pages';
import { Main } from './pages/Main.tsx';
import './styles/app.scss';

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Auth />} />
      <Route path="/main" element={<Main />} />
      <Route path="/organization" element={<Organization />} />
    </Routes>
  );
};
