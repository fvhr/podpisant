import React, { useState } from 'react';
import { DocumentsList, DocumentsMenu } from '../components';
import { DocumentModal } from '../components/documents/documents-modal';
import { Sidebar } from '../components/sidebar';

export const Documents: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<'all' | 'signed' | 'rejected' | 'in_progress'>('all');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleCreateDocument = (name: string, file: File) => {
    console.log('Создаем документ:', name, file);
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
        <DocumentsList activeTab={activeTab} />
      </main>
      {isModalOpen && (
        <DocumentModal onClose={() => setIsModalOpen(false)} onCreate={handleCreateDocument} />
      )}
    </div>
  );
};
