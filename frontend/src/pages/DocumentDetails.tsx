import React, { useState } from 'react';
import { FiArrowLeft } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { CreateStageModal, DocumentButtons, DocumentHeader, DocumentStages } from '../components';
import { Sidebar } from '../components/sidebar';

export const DocumentDetails: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isStageModalOpen, setIsStageModalOpen] = useState(false);
  const navigate = useNavigate();

  const employees = [
    'Иванов Иван Иванович',
    'Петрова Анна Сергеевна',
    'Сидоров Алексей Владимирович',
  ];

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleCreateStage = (stageData: { name: string; employees: string[] }) => {
    console.log('Создан новый этап:', stageData);
    setIsStageModalOpen(false);
  };

  return (
    <div className="documents-page">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />

      <main className={`content ${isSidebarOpen ? 'with-sidebar' : 'full-width'}`}>
        <div className="document-details-layout">
          <div className="document-content-section">
            <div onClick={() => navigate('/documents')} className="document-back">
              <FiArrowLeft /> Назад
            </div>
            <DocumentHeader />
            <DocumentStages />
          </div>

          <DocumentButtons onCreateStage={() => setIsStageModalOpen(true)} />
        </div>
      </main>

      {isStageModalOpen && (
        <CreateStageModal
          onClose={() => setIsStageModalOpen(false)}
          onCreate={handleCreateStage}
          employees={employees}
        />
      )}
    </div>
  );
};
