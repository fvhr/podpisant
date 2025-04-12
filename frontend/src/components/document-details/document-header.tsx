import React, { useState } from 'react';

export const DocumentHeader: React.FC = () => {
  const [documentStatus, setDocumentStatus] = useState<'in-progress' | 'signed' | 'canceled'>(
    'in-progress',
  );

  const documentData = {
    title: 'Договор аренды офиса №45-2023',
    createdAt: '2023-11-01T10:00:00',
    stages: [
      {
        id: 1,
        name: 'Юридический отдел',
        employees: [
          { id: 1, name: 'Иванов А.П.', status: 'approved' },
          { id: 2, name: 'Петрова Е.В.', status: 'pending' },
        ],
      },
      {
        id: 2,
        name: 'Финансовый отдел',
        employees: [{ id: 3, name: 'Сидоров М.К.', status: 'pending' }],
      },
    ],
  };

  return (
    <div className="document-header">
      <h1>{documentData.title}</h1>
      <div className="document-meta">
        <span>Создан: {new Date(documentData.createdAt).toLocaleDateString()}</span>
        <span className={`status-badge ${documentStatus}`}>
          {documentStatus === 'in-progress' && 'В процессе'}
          {documentStatus === 'signed' && 'Подписан'}
          {documentStatus === 'canceled' && 'Отклонен'}
        </span>
      </div>
    </div>
  );
};
