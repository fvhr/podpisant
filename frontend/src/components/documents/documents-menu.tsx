import { FiPlus } from 'react-icons/fi';

interface MenuProps {
  setActiveTab: (tab: 'all' | 'signed' | 'canceled' | 'in-progress') => void;
  activeTab: 'all' | 'signed' | 'canceled' | 'in-progress';
  onOpenModal: () => void;
}

export const DocumentsMenu = ({ activeTab, setActiveTab, onOpenModal }: MenuProps) => {
  return (
    <div className="documents-header">
      <div className="header-left">
        <h1>Документы</h1>
        <div className="tabs">
          <button
            className={`tab ${activeTab === 'all' ? 'active' : ''}`}
            onClick={() => setActiveTab('all')}>
            Все
          </button>
          <button
            className={`tab ${activeTab === 'signed' ? 'active' : ''}`}
            onClick={() => setActiveTab('signed')}>
            Подписанные
          </button>
          <button
            className={`tab ${activeTab === 'canceled' ? 'active' : ''}`}
            onClick={() => setActiveTab('canceled')}>
            Отмененные
          </button>
          <button
            className={`tab ${activeTab === 'in-progress' ? 'active' : ''}`}
            onClick={() => setActiveTab('in-progress')}>
            В процессе
          </button>
        </div>
      </div>

      <button onClick={onOpenModal} className="create-document-btn">
        <FiPlus className="icon" />
        Создать документ
      </button>
    </div>
  );
};
