import React, { useEffect, useState } from 'react';
import { FiArrowLeft } from 'react-icons/fi';
import { useLocation, useNavigate } from 'react-router-dom';
import { getAllEmployes } from '../api/employes';
import { CreateStageModal, DocumentButtons, DocumentHeader, DocumentStages } from '../components';
import { Sidebar } from '../components/sidebar';

interface Employee {
  id: number;
  fio: string;
  phone: string;
  email: string;
  type_notification: string;
  is_dep_admin: boolean;
  id_dep: number;
}

export const DocumentDetails: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isStageModalOpen, setIsStageModalOpen] = useState(false);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const navigate = useNavigate();
  const location = useLocation();

  const orgId = location.state?.orgId;

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleCreateStage = (stageData: { name: string; stageNumber: number, deadline: Date, user_ids: string[] }) => {
    console.log('Создан новый этап:', stageData);
    setIsStageModalOpen(false);
  };

  const handleBack = () => {
    if (orgId) {
      navigate(`/documents/${orgId}`);
    } else {
      navigate('/documents');
    }
  };

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const data = await getAllEmployes(orgId);
        setEmployees(data || []);
      } catch (err) {
        console.error(err);
      }
    };
    fetchEmployees();
  }, []);

  return (
    <div className="documents-page">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />

      <main className={`content ${isSidebarOpen ? 'with-sidebar' : 'full-width'}`}>
        <div className="document-details-layout">
          <div className="document-content-section">
            <div style={{ width: '70%' }}>
              <div onClick={handleBack} className="document-back">
                <FiArrowLeft /> Назад
              </div>
              <DocumentHeader />
              <DocumentStages />
            </div>

            <DocumentButtons onCreateStage={() => setIsStageModalOpen(true)} />
          </div>
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
