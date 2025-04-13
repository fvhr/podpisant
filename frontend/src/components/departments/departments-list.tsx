import React, { useEffect, useState } from 'react';
import { FiCheck, FiChevronDown, FiChevronUp } from 'react-icons/fi';
import { getAllDepartaments } from '../../api/departament';

type User = {
  id: number;
  fio: string; 
  hasAccess: boolean;
};

type Department = {
  id: number;
  name: string;
  users: User[];
};

export const DepartmentsList: React.FC = () => {
  const [expandedDepartment, setExpandedDepartment] = useState<number | null>(null);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const orgId = Number(localStorage.getItem('currentOrgId'));

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        setIsLoading(true);
        const data = await getAllDepartaments(orgId);
        setDepartments(data || []);
      } catch (err) {
        setError('Не удалось загрузить отделы');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDepartments();
  }, []);

  const toggleDepartment = (departmentId: number) => {
    setExpandedDepartment(expandedDepartment === departmentId ? null : departmentId);
  };

  const toggleUserAccess = (departmentId: number, userId: number) => {
    setDepartments((prevDepartments) =>
      prevDepartments.map((department) => {
        if (department.id === departmentId) {
          return {
            ...department,
            users: department.users.map((user) =>
              user.id === userId ? { ...user, hasAccess: !user.hasAccess } : user,
            ),
          };
        }
        return department;
      }),
    );
  };

  if (isLoading) {
    return <div className="access-control__loading">Загрузка отделов...</div>;
  }

  if (error) {
    return <div className="access-control__error">{error}</div>;
  }

  if (!departments || !Array.isArray(departments)) {
    return <div className="access-control__error">Ошибка: Некорректные данные отделов</div>;
  }

  return (
    <div className="access-control">
      <h1 className="access-control__title">Управление отделами</h1>

      {departments.length === 0 ? (
        <div className="access-control__empty">Нет доступных отделов</div>
      ) : (
        <div className="access-control__list">
          {departments.map((department) => (
            <div
              key={department.id}
              className={`access-control__department ${
                expandedDepartment === department.id ? 'expanded' : ''
              }`}>
              <div
                className="access-control__header"
                onClick={() => toggleDepartment(department.id)}>
                <h3>{department.name}</h3>
                <span className="access-control__icon">
                  {expandedDepartment === department.id ? <FiChevronUp /> : <FiChevronDown />}
                </span>
              </div>

              {expandedDepartment === department.id && (
                <div className="access-control__content">
                  {!department.users || department.users.length === 0 ? (
                    <div className="access-control__empty">Нет пользователей в отделе</div>
                  ) : (
                    department.users.map((user) => (
                      <div key={user.id} className="access-control__employee">
                        <span>{user.fio}</span>
                        <div className="access-control__checkbox-wrapper">
                          <span className="access-control__label">Групповая рассылка</span>
                          <label className="access-control__checkbox">
                            <input
                              type="checkbox"
                              checked={user.hasAccess}
                              onChange={() => toggleUserAccess(department.id, user.id)}
                            />
                            <span className="access-control__checkmark">
                              {user.hasAccess && <FiCheck />}
                            </span>
                          </label>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
