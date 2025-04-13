import React, { useEffect, useState } from 'react';
import { AiFillDelete, AiOutlinePlus } from 'react-icons/ai';
import { getAllDepartaments } from '../../api/departament';
import { getAllEmployes } from '../../api/employes';
import { addEmployee } from '../../api/user';
import { useProfile } from '../ProfileContext';
import { AddEmployeeModal } from './employes-modal';

interface Employee {
  id: number;
  fio: string;
  phone: string;
  email: string;
  type_notification: string;
  is_super_admin: boolean;
  id_dep: number;
}

export const EmployeesList: React.FC = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState({
    list: false,
    add: false,
    departments: false,
  });
  const [error, setError] = useState<string | null>(null);
  const [departments, setDepartments] = useState<{ id: number; name: string }[]>([]);
  const orgId = Number(localStorage.getItem('currentOrgId'));

  const { profile } = useProfile();

  const fetchEmployees = async () => {
    setIsLoading((prev) => ({ ...prev, list: true }));
    setError(null);
    try {
      const data = await getAllEmployes(orgId);
      setEmployees(data || []);
    } catch (err) {
      setError('Не удалось загрузить список сотрудников');
      console.error(err);
    } finally {
      setIsLoading((prev) => ({ ...prev, list: false }));
    }
  };

  const fetchDepartments = async () => {
    setIsLoading((prev) => ({ ...prev, departments: true }));
    try {
      const data = await getAllDepartaments(orgId);
      setDepartments(data || []);
    } catch (err) {
      setError('Не удалось загрузить отделы');
      console.error(err);
    } finally {
      setIsLoading((prev) => ({ ...prev, departments: false }));
    }
  };

  const handleAddEmployee = async (employeeData: {
    id_dep: number;
    fullName: string;
    phone: string;
    email: string;
    typeNotification: string;
    isAdmin: boolean;
  }) => {
    setIsLoading((prev) => ({ ...prev, add: true }));
    setError(null);

    try {
      await addEmployee(
        employeeData.id_dep,
        employeeData.fullName,
        employeeData.email,
        employeeData.phone,
        employeeData.typeNotification,
        employeeData.isAdmin,
      );
      await fetchEmployees();
      setIsModalOpen(false);
    } catch (err) {
      setError('Не удалось добавить сотрудника');
      console.error(err);
    } finally {
      setIsLoading((prev) => ({ ...prev, add: false }));
    }
  };

  useEffect(() => {
    fetchDepartments();
    fetchEmployees();
  }, []);

  return (
    <main className="employees-container">
      <div className="employees-header">
        <h1 className="employees-title">Все сотрудники</h1>
        {profile?.is_super_admin && (
          <button
            className="add-employee-btn"
            onClick={() => setIsModalOpen(true)}
            disabled={isLoading.add}>
            <AiOutlinePlus className="icon" />
            {isLoading.add ? 'Добавление...' : 'Добавить сотрудника'}
          </button>
        )}

        {error && <div className="error-message">{error}</div>}
      </div>

      {isLoading.list ? (
        <div className="loading-message">Загрузка сотрудников...</div>
      ) : (
        <div className="employees-grid">
          {employees.map((emp) => (
            <div key={emp.id} className="employee-card">
              <div className="employee-content">
                <h3 className="employee-name">{emp.fio}</h3>
                <div className="employee-contacts">
                  <p className="employee-phone">{emp.phone === null ? 'Не указан' : emp.phone} </p>
                  <p className="employee-email">{emp.email}</p>
                </div>
                <div className="employee-meta">
                  <span className={`employee-admin ${emp.is_super_admin ? 'admin' : ''}`}>
                    {emp.is_super_admin ? 'Администратор' : 'Сотрудник'}
                  </span>
                  <span className="employee-notification">
                    Предпочтительная связь:{' '}
                    {emp.type_notification === 'TG'
                      ? 'Telegram'
                      : emp.type_notification === 'EMAIL'
                      ? 'Email'
                      : 'SMS'}
                  </span>
                </div>
              </div>
              {profile?.is_super_admin &&
                <div className="employee-actions">
                  <AiFillDelete className="delete-icon" />
                  <span className="delete-text">Удалить</span>
                </div>
              }
            </div>
          ))}
        </div>
      )}

      {isModalOpen && (
        <AddEmployeeModal
          onClose={() => setIsModalOpen(false)}
          onAdd={handleAddEmployee}
          isLoading={isLoading.add}
          departments={departments}
        />
      )}
    </main>
  );
};
