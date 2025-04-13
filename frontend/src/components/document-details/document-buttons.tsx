import { FiDownload } from 'react-icons/fi';
interface DocumentButtonsProps {
  onCreateStage: () => void; 
}
export const DocumentButtons = ({ onCreateStage }: DocumentButtonsProps) => {
	
  return (
    <div className="document-actions">
      <button className="action-btn edit" onClick={onCreateStage}>
        Создать новый этап
      </button>
      <button className="action-btn download">
        <FiDownload /> Скачать документ
      </button>

      <button className="action-btn create">Подписать документ</button>
      <button className="action-btn canceled">Отклонить документ</button>
    </div>
  );
};
