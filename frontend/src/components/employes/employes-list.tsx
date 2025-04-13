import React, { useState } from 'react';
import { AiFillDelete, AiOutlinePlus } from 'react-icons/ai';
import { addEmployee } from '../../api/user';
import { AddEmployeeModal } from './employes-modal';

interface Employee {
  id: number;
  fio: string;
  phone: string;
  email: string;
  type_notification: string;
  is_dep_admin: boolean;
}

export const EmployeesList: React.FC = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleDeleteClick = (id: number, e: React.MouseEvent) => {
    e.stopPropagation();
    setEmployees((emps) => emps.filter((emp) => emp.id !== id));
  };

  const departaments = [
    { id: 1, name: 'Отдел разработки' },
    { id: 2, name: 'Отдел маркетинга' },
    { id: 3, name: 'Отдел продаж' },
    { id: 4, name: 'Финансовый отдел' },
    { id: 5, name: 'HR отдел' },
  ];

  const handleAddEmployee = async (employeeData: {
    fullName: string;
    phone: string;
    email: string;
    typeNotification: string;
    isAdmin: boolean;
  }) => {
    setIsLoading(true);
    setError(null);

    try {
      const newEmployee = await addEmployee(
        employeeData.fullName,
        employeeData.email,
        employeeData.phone,
        employeeData.typeNotification,
        employeeData.isAdmin,
      );

      setEmployees((prev) => [...prev, newEmployee]);
      setIsModalOpen(false);
    } catch (err) {
      setError('Не удалось добавить сотрудника');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="employees-container">
      <div className="employees-header">
        <h1 className="employees-title">Все сотрудники</h1>
        <button
          className="add-employee-btn"
          onClick={() => setIsModalOpen(true)}
          disabled={isLoading}>
          <AiOutlinePlus className="icon" />
          {isLoading ? 'Добавление...' : 'Добавить сотрудника'}
        </button>
        {error && <div className="error-message">{error}</div>}
      </div>

      <div className="employees-grid">
        {employees.map((emp) => (
          <div key={emp.id} className="employee-card">
            <div className="employee-content">
              <h3 className="employee-name">{emp.fio}</h3>
              <div className="employee-contacts">
                <p className="employee-phone">{emp.phone}</p>
                <p className="employee-email">{emp.email}</p>
              </div>
              <div className="employee-meta">
                <span className={`employee-admin ${emp.is_dep_admin ? 'admin' : ''}`}>
                  {emp.is_dep_admin ? 'Администратор' : 'Сотрудник'}
                </span>
                <span className="employee-notification">Уведомления: {emp.type_notification}</span>
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
          isLoading={isLoading}
          departments={departaments}
        />
      )}
    </main>
  );
};
