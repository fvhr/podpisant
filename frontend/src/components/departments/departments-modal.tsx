import React, { useState } from 'react';

type DepartmentModalProps = {
    onClose: () => void;
    onAdd: (departmentName: string) => void;
    isLoading: boolean;
};

export const DepartmentModal: React.FC<DepartmentModalProps> = ({ onClose, onAdd, isLoading }) => {
    const [departmentName, setDepartmentName] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (departmentName.trim()) {
            onAdd(departmentName);
        }
    };

    return (
        <div className="employee-modal-overlay">
            <div className="employee-modal-content">
                <div className="employee-modal-header">
                    <h2>Создать новый отдел</h2>
                    <button className="employee-modal-close-button" onClick={onClose}>
                        &times;
                    </button>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="employee-modal-form-group">
                        <label htmlFor="departmentName">Название отдела</label>
                        <input
                            id="departmentName"
                            type="text"
                            value={departmentName}
                            onChange={(e) => setDepartmentName(e.target.value)}
                            placeholder="Введите название отдела"
                            required
                        />
                    </div>

                    <div className="employee-modal-actions">
                        <button
                            type="submit"
                            className="employee-submit-button"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Создание...' : 'Создать отдел'}
                        </button>
                        <button
                            type="button"
                            className="employee-cancel-button"
                            onClick={onClose}
                        >
                            Отмена
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};