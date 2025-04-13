import { useState } from 'react';
import { FiDownload } from 'react-icons/fi';
import { downloadDocument } from '../../api/documents.ts';
import { useCallback } from 'react';
import { useProfile } from '../ProfileContext';

interface DocumentButtonsProps {
  onCreateStage: () => void;
  document_id: number;
  onSignDocument: () => Promise<void>;
  onRejectDocument: () => Promise<void>;
}

export const DocumentButtons = ({ onCreateStage, document_id }: DocumentButtonsProps) => {
    const handleDownload = useCallback(() => {
        console.log('Downloading document with ID:', document_id);
        downloadDocument(document_id);
    }, [document_id]);

    return (
        <div className="document-actions">
            <button className="action-btn edit" onClick={onCreateStage}>
                Создать новый этап
            </button>

            <button className="action-btn download" onClick={handleDownload}>
                <FiDownload /> Скачать документ
            </button>

            <button className="action-btn create" >Подписать документ</button>
            <button className="action-btn canceled">Отклонить документ</button>
        </div>
    );
};

export const DocumentButtons = ({
  onCreateStage,
  onSignDocument,
  onRejectDocument,
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

      <button className="action-btn download">
        <FiDownload /> Скачать документ
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