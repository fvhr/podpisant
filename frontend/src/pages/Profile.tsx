import React, { useState } from 'react';
import { Sidebar } from '../components';
import { ProfileElement } from '../components/profile/profile-element.tsx';

export const Profile: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };
  return (
    <div className="wrapper">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
      <main className={`content ${isSidebarOpen ? 'with-sidebar' : 'full-width'}`}>
        <ProfileElement />
      </main>
    </div>
  );
};
