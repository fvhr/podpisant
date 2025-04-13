import { useState } from 'react';
import { FiDownload } from 'react-icons/fi';
import { downloadDocument } from '../../api/documents.ts';
import { useProfile } from '../ProfileContext';

interface DocumentButtonsProps {
  onCreateStage: () => void;
  document_id: number;
  onSignDocument: () => Promise<void>;
  onRejectDocument: () => Promise<void>;
}

export const DocumentButtons = ({
  onCreateStage,
  onSignDocument,
  onRejectDocument,
  document_id
}: DocumentButtonsProps) => {
  const [isSigning, setIsSigning] = useState(false);
  const [isRejecting, setIsRejecting] = useState(false);

  const { profile } = useProfile();

  const handleRejectDocument = async () => {
    setIsRejecting(true);
    try {
      await onRejectDocument();
    } catch (error) {
      console.error('Ошибка при отклонении документа:', error);
    } finally {
      setIsRejecting(false);
    }
  };

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
        {profile?.is_super_admin && (
            <button className="action-btn edit" onClick={onCreateStage}>
              Создать новый этап
            </button>
        )}

        <button
            className="action-btn download"
            onClick={() => downloadDocument(document_id)}
        >
          <FiDownload/> Скачать документ
        </button>

        <button
            className={`action-btn create ${isSigning ? 'disabled' : ''}`}
            onClick={handleSignDocument}
            disabled={isSigning}>
          {isSigning ? 'Подпись...' : 'Подписать документ'}
        </button>
        <button className="action-btn canceled" disabled={isRejecting} onClick={handleRejectDocument}>
          Отклонить документ
        </button>
      </div>
  );
};