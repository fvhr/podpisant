import { useEffect, useState } from 'react';
import { FiClock } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { getAllDocuments } from '../../api/documents';
import { Document } from '../../types';

interface DocumentsListProps {
  activeTab: 'all' | 'signed' | 'rejected' | 'in_progress';
}

export const DocumentsList = ({ activeTab }: DocumentsListProps) => {
  const [documents, setDocuments] = useState<Document[]>([]);
  const [filteredDocuments, setFilteredDocuments] = useState<Document[]>([]);
  const navigate = useNavigate();

  function getStatusLabel(status: string): string {
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
  }

  useEffect(() => {
    if (activeTab === 'all') {
      setFilteredDocuments(documents);
    } else {
      setFilteredDocuments(documents.filter((doc) => doc.status === activeTab));
    }
  }, [activeTab, documents]);

  useEffect(() => {
    const fetchDocuments = async () => {
      try {
        const res = await getAllDocuments();
        setDocuments(res);
      } catch {
        console.log('Ошибка загрузки документов');
      }
    };
    fetchDocuments();
  }, []);

  return (
    <div className="documents-grid">
      {filteredDocuments.length === 0 ? (
        <div className="empty-state">
          {documents.length === 0 ? 'Документов пока нет' : 'Нет документов по выбранному фильтру'}
        </div>
      ) : (
        filteredDocuments.map((document) => (
          <div
            onClick={() => navigate('/document')}
            key={document.id}
            className={`document-card ${document.status}`}>
            <div className="document-info">
              <h3>{document.name}</h3>
              <div className="document-meta">
                <span className="document-date">
                  <FiClock className="icon" /> {new Date(document.created_at).toLocaleString()}
                </span>
                <span className={`document-status ${document.status}`}>
                  {getStatusLabel(document.status)}
                </span>
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
};
