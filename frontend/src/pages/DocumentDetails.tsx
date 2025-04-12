import React, { useState } from 'react';
import { FiArrowLeft } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { DocumentButtons, DocumentHeader, DocumentStages } from '../components';
import { Sidebar } from '../components/sidebar';

export const DocumentDetails: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const navigate = useNavigate();

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };
  return (
    <div className="documents-page">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />

      <main className={`content ${isSidebarOpen ? 'with-sidebar' : 'full-width'}`}>
        <div className="document-details-layout">
          <div style={{ width: '70%' }}>
            <div onClick={() => navigate('/documents')} className="document-back">
              <FiArrowLeft /> Назад
            </div>
            <DocumentHeader />

            <DocumentStages />
          </div>
          <DocumentButtons />
        </div>
      </main>
    </div>
  );
};
