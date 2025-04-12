import { useEffect, useMemo, useState } from 'react';
import { FiClock } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { getAllDocuments } from '../../api/documents';
import { Document } from '../../types';

interface DocumentsListProps {
  activeTab: 'all' | 'signed' | 'rejected' | 'in_progress';
  refreshTrigger?: number;
}

export const DocumentsList = ({ activeTab, refreshTrigger = 0 }: DocumentsListProps) => {
  const [documents, setDocuments] = useState<Document[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  const getStatusLabel = (status: string): string => {
    switch (status) {
      case 'signed':
        return 'Подписан';
      case 'rejected':
        return 'Отменен';
      case 'in_progress':
        return 'В процессе';
      default:
        return '';
    }
  };

  useEffect(() => {
    const fetchDocuments = async () => {
      setIsLoading(true);
      try {
        const res = await getAllDocuments();
        setDocuments(res);
      } catch (error) {
        console.error('Ошибка загрузки документов:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDocuments();
  }, [refreshTrigger]);

  const filteredDocuments = useMemo(() => {
    return activeTab === 'all' ? documents : documents.filter((doc) => doc.status === activeTab);
  }, [documents, activeTab]);

  if (isLoading) {
    return <div className="empty-state">Загрузка документов...</div>;
  }

  if (filteredDocuments.length === 0) {
    return (
      <div className="empty-state">
        {documents.length === 0 ? 'Документов пока нет' : 'Нет документов по выбранному фильтру'}
      </div>
    );
  }

  return (
    <div className="documents-grid">
      {filteredDocuments.map((document) => (
        <div
          onClick={() => navigate('/document')}
          key={document.id}
          className={`document-card ${document.status}`}>
          <div className="document-info">
            <h3>{document.name}</h3>
            <div className="document-meta">
              <span className="document-date">
                <FiClock className="icon" />
                {new Date(document.created_at).toLocaleString()}
              </span>
              <span className={`document-status ${document.status}`}>
                {getStatusLabel(document.status)}
              </span>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};
