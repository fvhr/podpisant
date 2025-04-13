import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getDocumentInfo } from '../../api/documents';

type Employee = {
  id: number;
  name: string;
  status: 'approved' | 'pending' | 'rejected';
};

type Stage = {
  id: number;
  name: string;
  employees: Employee[];
};

type Document = {
  id: number;
  name: string;
  created_at: string;
  status: 'in_progress' | 'signed' | 'rejected';
  stages?: Stage[];
};

export const DocumentHeader: React.FC = () => {
  const [document, setDocument] = useState<Document | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { docId } = useParams();
  const documentId = Number(docId);

  useEffect(() => {
    const fetchCurrentDocument = async () => {
      try {
        setIsLoading(true);
        const data = await getDocumentInfo(documentId);
        setDocument(data);
      } catch (err) {
        console.error(err);
        setError('Не удалось загрузить данные документа');
      } finally {
        setIsLoading(false);
      }
    };
    fetchCurrentDocument();
  }, [documentId]);

  if (isLoading) {
    return <div className="document-header loading">Загрузка документа...</div>;
  }

  if (error) {
    return <div className="document-header error">{error}</div>;
  }

  if (!document) {
    return <div className="document-header error">Документ не найден</div>;
  }

  const getStatusText = (status: string) => {
    switch (status) {
      case 'in_progress':
        return 'В процессе';
      case 'signed':
        return 'Подписан';
      case 'rejected':
        return 'Отклонен';
      default:
        return status;
    }
  };

  return (
    <div className="document-header">
      <h1>{document.name}</h1>
      <div className="document-meta">
        <span>Создан: {new Date(document.created_at).toLocaleDateString()}</span>
        <span className={`status-badge ${document.status}`}>{getStatusText(document.status)}</span>
      </div>
    </div>
  );
};
