import React, { useState } from 'react';
import { AiFillDelete, AiOutlinePlus } from 'react-icons/ai';
import { AddEmployeeModal } from './employes-modal';

interface Employee {
  id: number;
  fullName: string;
  phone: string;
  email: string;
  department: string;
}

const departments = [
  'Отдел продаж',
  'Маркетинг',
  'ИТ отдел',
  'Финансовый отдел',
  'HR',
  'Логистика',
  'Производство',
];

export const EmployeesList: React.FC = () => {
  const [employees, setEmployees] = useState<Employee[]>([
    {
      id: 1,
      fullName: 'Иван Иванов',
      phone: '+7 123 456 78 90',
      email: 'ivanov@example.com',
      department: 'Отдел продаж',
    },
  ]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleDeleteClick = (id: number, e: React.MouseEvent) => {
    e.stopPropagation();
    setEmployees((emps) => emps.filter((emp) => emp.id !== id));
  };

  const handleAddEmployee = (newEmployee: Omit<Employee, 'id'>) => {
    const employee: Employee = {
      id: employees.length + 1,
      ...newEmployee,
    };
    setEmployees([...employees, employee]);
  };

  return (
    <main className="employees-container">
      <div className="employees-header">
        <h1 className="employees-title">Все сотрудники</h1>
        <button className="add-employee-btn" onClick={() => setIsModalOpen(true)}>
          <AiOutlinePlus className="icon" />
          Добавить сотрудника
        </button>
      </div>

      <div className="employees-grid">
        {employees.map((emp) => (
          <div key={emp.id} className="employee-card">
            <div className="employee-content">
              <h3 className="employee-name">{emp.fullName}</h3>
              <p className="employee-department">{emp.department}</p>
              <div className="employee-contacts">
                <p className="employee-phone">{emp.phone}</p>
                <p className="employee-email">{emp.email}</p>
              </div>
            </div>
            <div className="employee-actions" onClick={(e) => handleDeleteClick(emp.id, e)}>
              <AiFillDelete className="delete-icon" />
              <span className="delete-text">Удалить</span>
            </div>
          </div>
        ))}
      </div>

      {isModalOpen && (
        <AddEmployeeModal
          onClose={() => setIsModalOpen(false)}
          onAdd={handleAddEmployee}
          departments={departments}
        />
      )}
    </main>
  );
};
