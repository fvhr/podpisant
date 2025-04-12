import { FiClock } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { Document } from '../../types/documents';

interface DocumentsListProps {
  documents: Document[];
  activeTab: 'all' | 'signed' | 'canceled' | 'in-progress';
}

export const DocumentsList = ({ documents, activeTab }: DocumentsListProps) => {
  const navigate = useNavigate();

  function getStatusLabel(status: string): string {
    switch (status) {
      case 'signed':
        return 'Подписан';
      case 'canceled':
        return 'Отменен';
      case 'in-progress':
        return 'В процессе';
      default:
        return '';
    }
  }

  return (
    <div className="documents-grid">
      {documents
        .filter((doc) => activeTab === 'all' || doc.status === activeTab)
        .map((document) => (
          <div
            onClick={() => navigate('/document')}
            key={document.id}
            className={`document-card ${document.status}`}>
            <div className="document-info">
              <h3>{document.title}</h3>
              <div className="document-meta">
                <span className="document-date">
                  <FiClock className="icon" /> {new Date(document.date).toLocaleString()}
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
