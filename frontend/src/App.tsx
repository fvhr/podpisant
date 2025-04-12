import { Navigate } from 'react-router-dom';
import { Auth } from './pages';
import {Route, Routes} from "react-router-dom";
import './styles/app.scss';
import {Main} from "./pages/Main.tsx";

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Auth />} />
      <Route path="/main" element={<Main />} />
    </Routes>
  );
};
