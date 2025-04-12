import React from 'react';
import { FiDownload } from 'react-icons/fi';

export const DocumentButtons: React.FC = () => {
  return (
    <div className="document-actions">
      <button className="action-btn edit">Создать новый этап</button>
      <button className="action-btn download">
        <FiDownload /> Скачать документ
      </button>

      <button className="action-btn create">Подписать документ</button>
      <button className="action-btn canceled">Отклонить документ</button>
    </div>
  );
};
