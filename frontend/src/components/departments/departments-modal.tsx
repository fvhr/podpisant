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
        <div className="modal">
            <div className="modal-content">
                <h2>Создать новый отдел</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="departmentName">Название отдела</label>
                        <input
                            id="departmentName"
                            type="text"
                            value={departmentName}
                            onChange={(e) => setDepartmentName(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" disabled={isLoading}>
                        {isLoading ? 'Создание...' : 'Создать отдел'}
                    </button>
                    <button type="button" onClick={onClose}>
                        Отменить
                    </button>
                </form>
            </div>
        </div>
    );
};