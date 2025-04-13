import React, { useEffect, useState } from 'react';
import { FiArrowLeft } from 'react-icons/fi';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import { rejectDocument, signatureDocument } from '../api/documents';
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
  const [refreshTrigger, setRefreshTrigger] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();
  const { docId } = useParams();
  const documentId = Number(docId);

  const orgId = location.state?.orgId;

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleCreateStage = () => {
    setIsStageModalOpen(false);
  };

  const handleBack = () => {
    if (orgId) {
      navigate(`/documents/${orgId}`);
    } else {
      navigate('/documents');
    }
  };

  const handleSignDocument = async () => {
    try {
      const stageId = localStorage.getItem('currentStageId');
      if (stageId) {
        const stageIdNumber = +stageId;
        await signatureDocument(documentId, stageIdNumber);
        setRefreshTrigger((prev) => !prev);
      }
    } catch (error) {
      console.error('Ошибка при подписании документа:', error);
      alert('Не удалось подписать документ');
    }
  };
  const handleRejectDocument = async () => {
    try {
      const stageId = localStorage.getItem('currentStageId');
      if (stageId) {
        const stageIdNumber = +stageId;
        await rejectDocument(documentId, stageIdNumber);
        setRefreshTrigger((prev) => !prev);
        alert('Документ успешно отклонен');
      }
    } catch (error) {
      console.error('Ошибка при отклонении документа:', error);
      alert('Не удалось отклонить документ');
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
  }, [orgId]);

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
              <DocumentStages refreshTrigger={refreshTrigger} />
            </div>

            <DocumentButtons
              onCreateStage={() => setIsStageModalOpen(true)}
              onSignDocument={handleSignDocument}
              onRejectDocument={handleRejectDocument}
              document_id={documentId}
            />
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
