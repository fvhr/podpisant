import React, { useState } from 'react';
import { Sidebar } from '../components';
import { EmployeesList } from '../components/employes/employes-list.tsx';

export const Employes: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };
  return (
    <div className="wrapper">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
      <main className={`content ${isSidebarOpen ? 'with-sidebar' : 'full-width'}`}>
        <EmployeesList />
      </main>
    </div>
  );
};
