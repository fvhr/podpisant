import React, { useEffect, useState } from 'react';
import { FiCheck, FiChevronDown, FiChevronUp, FiX } from 'react-icons/fi';
import { useParams } from 'react-router-dom';
import { getDocumentOrganization } from '../../api/documents';

type Signature = {
  user_id: string;
  fio: string;
  status: 'signed' | 'in_progress' | 'rejected';
  signed_at?: string;
};

type Stage = {
  id: number;
  name: string;
  signatures: Signature[];
};

type DocumentData = Stage[];

export const DocumentStages: React.FC = () => {
  const [expandedStage, setExpandedStage] = useState<number | null>(null);
  const [documentData, setDocumentData] = useState<DocumentData>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { docId } = useParams<{ docId?: string }>();

  useEffect(() => {
    const fetchDocumentStages = async () => {
      try {
        setIsLoading(true);
        const documentId = docId ? parseInt(docId, 10) : null;

        if (!documentId || isNaN(documentId)) {
          throw new Error('Неверный ID документа');
        }

        const response = await getDocumentOrganization(documentId);
        setDocumentData(response);
      } catch (err) {
        console.error('Ошибка загрузки стадий:', err);
        setError('Не удалось загрузить данные документа');
      } finally {
        setIsLoading(false);
      }
    };

    fetchDocumentStages();
  }, [docId]);

  const toggleStage = (stageId: number) => {
    setExpandedStage(expandedStage === stageId ? null : stageId);
  };

  if (isLoading) {
    return <div className="loading">Загрузка информации о документе...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!documentData || documentData.length === 0) {
    return <div className="error">Данные документа не найдены</div>;
  }

  return (
    <div className="document-details">
      <div className="document-stages">
        {documentData.map((stage) => (
          <div key={stage.id} className="stage-card">
            <div className="stage-header" onClick={() => toggleStage(stage.id)}>
              <h3>{stage.name}</h3>
              <span className="toggle-icon">
                {expandedStage === stage.id ? <FiChevronUp /> : <FiChevronDown />}
              </span>
            </div>

            {expandedStage === stage.id && (
              <div className="stage-content">
                <ul className="employees-list">
                  {stage.signatures.map((signature) => (
                    <li key={signature.user_id} className="employee-item">
                      <span>{signature.fio}</span>
                      <div className="status-indicator">
                        {signature.status === 'signed' && <FiCheck className="approved" />}
                        {signature.status === 'rejected' && <FiX className="rejected" />}
                        {signature.status === 'in_progress' && (
                          <span className="pending">Ожидает</span>
                        )}
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};
