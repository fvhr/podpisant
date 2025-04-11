import {Route, Routes} from "react-router-dom";
import './styles/app.scss';
import {Main} from "./pages/Main.tsx";
import {Header} from "./components/header.tsx";

export const App = () => {
  return (
	  <>
	  	<Header />
		<Routes>
			<Route path="/" element={<Main />} />
			<Route path="/about" element={<div>О нас</div>} />
		</Routes>
	  </>
  );
};