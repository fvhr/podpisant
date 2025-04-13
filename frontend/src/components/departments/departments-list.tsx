import React, { useState } from 'react';
import { FiCheck, FiChevronDown, FiChevronUp } from 'react-icons/fi';

type Employee = {
  id: number;
  name: string;
  hasAccess: boolean;
};

type Department = {
  id: number;
  name: string;
  employees: Employee[];
};

export const DepartmentsList: React.FC = () => {
  const [expandedStage, setExpandedStage] = useState<number | null>(null);
  const [departments, setDepartments] = useState<Department[]>([
    {
      id: 1,
      name: 'Юридический отдел',
      employees: [
        { id: 1, name: 'Иванов А.П.', hasAccess: true },
        { id: 2, name: 'Петрова Е.В.', hasAccess: false },
      ],
    },
    {
      id: 2,
      name: 'Финансовый отдел',
      employees: [{ id: 3, name: 'Сидоров М.К.', hasAccess: false }],
    },
    {
      id: 3,
      name: 'Отдел кадров',
      employees: [{ id: 4, name: 'Гулякин А.М.', hasAccess: true }],
    },
  ]);

  const toggleStage = (stageId: number) => {
    setExpandedStage(expandedStage === stageId ? null : stageId);
  };

  const toggleEmployeeAccess = (deptId: number, employeeId: number) => {
    setDepartments((prev) =>
      prev.map((dept) =>
        dept.id === deptId
          ? {
              ...dept,
              employees: dept.employees.map((emp) =>
                emp.id === employeeId ? { ...emp, hasAccess: !emp.hasAccess } : emp,
              ),
            }
          : dept,
      ),
    );
  };

  return (
    <div className="access-control">
      <h1 className="access-control__title">Управление отделами</h1>

      <div className="access-control__list">
        {departments.map((department) => (
          <div
            key={department.id}
            className={`access-control__department ${
              expandedStage === department.id ? 'expanded' : ''
            }`}>
            <div className="access-control__header" onClick={() => toggleStage(department.id)}>
              <h3>{department.name}</h3>
              <span className="access-control__icon">
                {expandedStage === department.id ? <FiChevronUp /> : <FiChevronDown />}
              </span>
            </div>

            {expandedStage === department.id && (
              <div className="access-control__content">
                {department.employees.map((employee) => (
                  <div key={employee.id} className="access-control__employee">
                    <span>{employee.name}</span>
                    <div className="access-control__checkbox-wrapper">
                      <span className="access-control__label">Групповая рассылка</span>
                      <label className="access-control__checkbox">
                        <input
                          type="checkbox"
                          checked={employee.hasAccess}
                          onChange={() => toggleEmployeeAccess(department.id, employee.id)}
                        />
                        <span className="access-control__checkmark">
                          <FiCheck />
                        </span>
                      </label>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};
