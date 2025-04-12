import React, { useState } from 'react';
import { AiFillDelete } from 'react-icons/ai';
import { useNavigate } from 'react-router-dom';

interface Employee {
    id: number;
    fullName: string;
    phone: string;
    email: string;
    department: string;
}

export const EmployeesComponent: React.FC = () => {
    const navigate = useNavigate();
    const [employees, setEmployees] = useState<Employee[]>([
        {
            id: 1,
            fullName: 'Иван Иванов',
            phone: '+7 123 456 78 90',
            email: 'ivanov@example.com',
            department: 'Отдел продаж',
        },
        {
            id: 2,
            fullName: 'Мария Петрова',
            phone: '+7 987 654 32 10',
            email: 'petrova@example.com',
            department: 'Маркетинг',
        },
        {
            id: 3,
            fullName: 'Алексей Сидоров',
            phone: '+7 555 123 45 67',
            email: 'sidorov@example.com',
            department: 'ИТ отдел',
        },
        {
            id: 4,
            fullName: 'Елена Кузнецова',
            phone: '+7 888 765 43 21',
            email: 'kuznetsova@example.com',
            department: 'Финансовый отдел',
        },
        {
            id: 5,
            fullName: 'Дмитрий Смирнов',
            phone: '+7 234 567 89 01',
            email: 'smirnov@example.com',
            department: 'HR',
        },
    ]);

    const handleEmployeeClick = (id: number) => {
        navigate(`/employee/${id}`);
    };

    const handleDeleteClick = (id: number, e: React.MouseEvent) => {
        e.stopPropagation();
        setEmployees((emps) => emps.filter((emp) => emp.id !== id));
    };

    return (
        <main className="employees-container">
            <div className="employees-list">
                {employees.map((emp) => (
                    <div key={emp.id} className="employee-card">
                        <div className="employee-content" onClick={() => handleEmployeeClick(emp.id)}>
                            <h3 className="employee-name">{emp.fullName}</h3>
                            <p className="employee-phone">Телефон: {emp.phone}</p>
                            <p className="employee-email">Email: {emp.email}</p>
                            <p className="employee-department">Отдел: {emp.department}</p>
                        </div>
                        <div className="employee-actions" onClick={(e) => handleDeleteClick(emp.id, e)}>
                            <AiFillDelete className="delete-icon" />
                        </div>
                    </div>
                ))}
            </div>
        </main>
    );
};