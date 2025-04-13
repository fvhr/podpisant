import { useEffect, useState } from 'react';
import { FiCheck, FiChevronDown, FiChevronUp, FiX } from 'react-icons/fi';
import { useParams } from 'react-router-dom';
import { getDocumentOrganization } from '../../api/documents';

type Signature = {
  user_id: string;
  fio: string;
  status: 'signed' | 'in_progress' | 'rejected';
  signed_at?: string;
  signature_type: string | null;
  rejected_at: string | null;
};

type Stage = {
  id: number;
  name: string;
  number: number;
  is_current: boolean;
  signatures: Signature[];
};

type DocumentData = Stage[];

interface Props {
  refreshTrigger: boolean;
}

export const DocumentStages = ({ refreshTrigger }: Props) => {
  const [expandedStage, setExpandedStage] = useState<number | null>(null);
  const [documentData, setDocumentData] = useState<DocumentData>([]);
  const [currentStage, setCurrentStage] = useState<Stage | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { docId } = useParams<{ docId?: string }>();

  const fetchDocumentStages = async () => {
    try {
      setIsLoading(true);
      const documentId = docId ? parseInt(docId, 10) : null;

      if (!documentId || isNaN(documentId)) {
        throw new Error('Неверный ID документа');
      }

      const response = await getDocumentOrganization(documentId);
      setDocumentData(response);

      const activeStage = response.find((stage: Stage) => stage.is_current);
      if (activeStage) {
        setCurrentStage(activeStage);
        setExpandedStage(activeStage.id);
        localStorage.setItem('currentStageId', activeStage.id.toString());
      }
    } catch (err) {
      console.error('Ошибка загрузки стадий:', err);
      setError('Не удалось загрузить данные документа');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchDocumentStages();
  }, [docId, refreshTrigger]);

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
  console.log(currentStage);

  return (
    <div className="document-details">
      <div className="document-stages">
        {documentData.map((stage) => (
          <div key={stage.id} className={`stage-card ${stage.is_current ? 'current-stage' : ''}`}>
            <div className="stage-header" onClick={() => toggleStage(stage.id)}>
              <h3>
                Этап {stage.number}: {stage.name}
                {stage.is_current && <span className="current-badge">Текущий</span>}
              </h3>
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
                        {signature.signature_type !== null && signature.rejected_at === null && (
                          <FiCheck style={{ color: 'green', fontSize: '1.2rem' }} />
                        )}
                        {signature.signature_type === 'rejected' && (
                          <FiX style={{ color: 'red' }} />
                        )}
                        {signature.signature_type === null && (
                          <span className="pending">ожидает...</span>
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
