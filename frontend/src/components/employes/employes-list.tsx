import React, { useEffect, useState } from 'react';
import { AiFillDelete, AiOutlinePlus } from 'react-icons/ai';
import { getAllDepartaments } from '../../api/departament';
import { getAllEmployes, deleteUserFromDepartment } from '../../api/employes';
import { addEmployee } from '../../api/user';
import { AddEmployeeModal } from './employes-modal';

interface Employee {
    id: string;
    fio: string;
    phone: string;
    email: string;
    type_notification: string;
    is_super_admin: boolean;
    departments: { department_id: string; department_name: string; is_admin: boolean }[];
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

    const handleDeleteEmployee = async (userUuid: string, depId: number) => {
        if (!userUuid) {
            console.error('Ошибка: Не хватает данных для удаления');
            return;
        }

        setIsLoading((prev) => ({ ...prev, list: true }));
        setError(null);

        try {
            await deleteUserFromDepartment(userUuid, depId);
            await fetchEmployees();
        } catch (err) {
            setError('Не удалось удалить сотрудника');
            console.error(err);
        } finally {
            setIsLoading((prev) => ({ ...prev, list: false }));
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
                <button
                    className="add-employee-btn"
                    onClick={() => setIsModalOpen(true)}
                    disabled={isLoading.add}
                >
                    <AiOutlinePlus className="icon" />
                    {isLoading.add ? 'Добавление...' : 'Добавить сотрудника'}
                </button>
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
                                    <p className="employee-phone">{emp.phone || 'Не указан'}</p>
                                    <p className="employee-email">{emp.email}</p>
                                </div>
                                <div className="employee-meta">
                  <span className={`employee-admin ${emp.is_super_admin ? 'admin' : ''}`}>
                    {emp.is_super_admin ? 'Супер Админ' : 'Сотрудник'}
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

                            {!emp.is_super_admin && (
                                <div
                                    className="employee-actions"
                                    onClick={() => {
                                        if (emp.id) {
                                            const depId = emp.departments.length > 0 ? Number(emp.departments[0].department_id) : 0;
                                            handleDeleteEmployee(emp.id, depId);
                                        } else {
                                            console.error('Ошибка: id не определен');
                                        }
                                    }}
                                >
                                    <AiFillDelete className="delete-icon" />
                                    <span className="delete-text">Удалить</span>
                                </div>
                            )}
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