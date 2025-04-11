import {Route, Routes} from "react-router-dom";
import './styles/app.scss';
import {Main} from "./pages/Main.tsx";
import {Header} from "./components/header.tsx";
import {useState} from "react";

export const App = () => {

	const [isSidebarOpen, setIsSidebarOpen] = useState(true);

	const toggleSidebar = () => {
		setIsSidebarOpen(!isSidebarOpen);
	};

  return (
	  <>
	  	<Header
			isSidebarOpen={isSidebarOpen}
			toggleSidebar={toggleSidebar}
		/>
		<Routes>
			<Route path="/" element={<Main />} />
			<Route path="/about" element={<div>О нас</div>} />
		</Routes>
	  </>
  );
};