import React, { useState } from 'react';
import { Sidebar } from '../components/sidebar';

export const Organization: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };
  return (
    <div className="wrapper">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
    </div>
  );
};
