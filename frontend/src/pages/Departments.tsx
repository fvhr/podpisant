import React, { useState } from 'react';
import { Sidebar } from '../components';
import {DepartmentsList} from "../components/departments/departments-list.tsx";

export const Departments: React.FC = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };
    return (
        <div className="wrapper">
            <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
            <main className={`content ${isSidebarOpen ? 'with-sidebar' : 'full-width'}`}>
                <DepartmentsList />
            </main>
        </div>
    );
};
