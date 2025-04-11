import { Route, Routes } from "react-router-dom";
import './styles/app.scss';

export const App = () => {
  return (
		<Routes>
			<Route path="/" element={<div>Главная страница</div>} />
			<Route path="/about" element={<div>О нас</div>} />
		</Routes>
  );
};