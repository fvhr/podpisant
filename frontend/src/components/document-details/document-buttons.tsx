import { FiDownload } from 'react-icons/fi';
import { downloadDocument } from '../../api/documents.ts';
import { useCallback } from 'react';

interface DocumentButtonsProps {
    onCreateStage: () => void;
    document_id: number;
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