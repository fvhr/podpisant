import React, { useState } from 'react';
import { DocumentsList, DocumentsMenu } from '../components';
import { DocumentModal } from '../components/documents/documents-modal';
import { Sidebar } from '../components/sidebar';

export const Documents: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<'all' | 'signed' | 'rejected' | 'in_progress'>('all');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleRefresh = () => {
    setRefreshTrigger((prev) => prev + 1); // Увеличиваем триггер для обновления списка
  };

  return (
    <div className="documents-page">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />

      <main className={`content ${isSidebarOpen ? 'with-sidebar' : 'full-width'}`}>
        <DocumentsMenu
          onOpenModal={() => setIsModalOpen(true)}
          setActiveTab={setActiveTab}
          activeTab={activeTab}
        />
        <DocumentsList activeTab={activeTab} refreshTrigger={refreshTrigger} />
      </main>

      {isModalOpen && (
        <DocumentModal
          onClose={() => setIsModalOpen(false)}
          onSuccess={handleRefresh} 
        />
      )}
    </div>
  );
};
