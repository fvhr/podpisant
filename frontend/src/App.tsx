import { Navigate, Route, Routes } from 'react-router-dom';
import { Auth } from './pages';
import './styles/app.scss';

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Auth />} />
    </Routes>
  );
};
