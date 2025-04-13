import React from 'react';
import ReactDOM from 'react-dom';

interface MainDeleteModalProps {
    onClose: () => void;
    onConfirm: () => void;
}

export const MainDeleteModal: React.FC<MainDeleteModalProps> = ({ onClose, onConfirm }) => {
    return ReactDOM.createPortal(
        <div className="org-modal-overlay" onClick={onClose}>
            <div className="org-modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="org-modal-header">
                    <h2>Удалить организацию</h2>
                    <button className="org-modal-close-button" onClick={onClose} aria-label="Закрыть">
                        &times;
                    </button>
                </div>
                <p className="org-modal-text">Вы уверены, что хотите удалить эту организацию? Это действие нельзя отменить.</p>
                <div className="org-modal-actions">
                    <button onClick={onConfirm} className="org-modal-delete-button">
                        Удалить
                    </button>
                    <button onClick={onClose} className="org-modal-cancel-button">
                        Отмена
                    </button>
                </div>
            </div>
        </div>,
        document.body
    );
};