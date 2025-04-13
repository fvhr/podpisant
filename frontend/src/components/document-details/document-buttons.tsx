import { useState } from 'react';
import { FiDownload } from 'react-icons/fi';
import { useProfile } from '../ProfileContext';

interface DocumentButtonsProps {
  onCreateStage: () => void;
  onSignDocument: () => Promise<void>;
}

export const DocumentButtons = ({ onCreateStage, onSignDocument }: DocumentButtonsProps) => {
  const [isSigning, setIsSigning] = useState(false);

  const { profile } = useProfile();

  const handleSignDocument = async () => {
    setIsSigning(true);
    try {
      await onSignDocument();
      alert('Вы успешно поставили электронную подпись!');
    } catch (error) {
      console.error('Ошибка при подписании документа:', error);
      alert('Не удалось подписать документ');
    } finally {
      setIsSigning(false);
    }
  };

  return (
    <div className="document-actions">
      { profile?.is_super_admin &&
        <button className="action-btn edit" onClick={onCreateStage}>
          Создать новый этап
        </button>
      }

      <button className="action-btn download">
        <FiDownload /> Скачать документ
      </button>

      <button
        className={`action-btn create ${isSigning ? 'disabled' : ''}`}
        onClick={handleSignDocument}
        disabled={isSigning}>
        {isSigning ? 'Подпись...' : 'Подписать документ'}
      </button>
      <button className="action-btn canceled">Отклонить документ</button>
    </div>
  );
};
