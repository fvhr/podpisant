import { FiPlus } from 'react-icons/fi';
import { useProfile } from '../ProfileContext';

interface MenuProps {
  setActiveTab: (tab: 'all' | 'signed' | 'rejected' | 'in_progress') => void;
  activeTab: 'all' | 'signed' | 'rejected' | 'in_progress';
  onOpenModal: () => void;
}

export const DocumentsMenu = ({ activeTab, setActiveTab, onOpenModal }: MenuProps) => {
  const { profile } = useProfile();
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
            className={`tab ${activeTab === 'rejected' ? 'active' : ''}`}
            onClick={() => setActiveTab('rejected')}>
            Отмененные
          </button>
          <button
            className={`tab ${activeTab === 'in_progress' ? 'active' : ''}`}
            onClick={() => setActiveTab('in_progress')}>
            В процессе
          </button>
        </div>
      </div>
      {profile?.is_super_admin && (
        <button onClick={onOpenModal} className="create-document-btn">
          <FiPlus className="icon" />
          Создать документ
        </button>
      )}
    </div>
  );
};
