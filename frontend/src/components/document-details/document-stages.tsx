import React, { useState } from 'react';
import { FiCheck, FiChevronDown, FiChevronUp, FiX } from 'react-icons/fi';

export const DocumentStages: React.FC = () => {
  const [expandedStage, setExpandedStage] = useState<number | null>(null);

  const documentData = {
    title: 'Договор аренды офиса №45-2023',
    createdAt: '2023-11-01T10:00:00',
    stages: [
      {
        id: 1,
        name: 'Юридический отдел',
        employees: [
          { id: 1, name: 'Иванов А.П.', status: 'approved' },
          { id: 2, name: 'Петрова Е.В.', status: 'pending' },
        ],
      },
      {
        id: 2,
        name: 'Финансовый отдел',
        employees: [{ id: 3, name: 'Сидоров М.К.', status: 'pending' }],
      },
    ],
  };

  const toggleStage = (stageId: number) => {
    setExpandedStage(expandedStage === stageId ? null : stageId);
  };
  return (
    <div className="document-stages">
      {documentData.stages.map((stage) => (
        <div key={stage.id} className="stage-card">
          <div className="stage-header" onClick={() => toggleStage(stage.id)}>
            <h3>{stage.name}</h3>
            <span className="toggle-icon">
              {expandedStage === stage.id ? <FiChevronUp /> : <FiChevronDown />}
            </span>
          </div>

          {expandedStage === stage.id && (
            <div className="stage-content">
              <ul className="employees-list">
                {stage.employees.map((employee) => (
                  <li key={employee.id} className="employee-item">
                    <span>{employee.name}</span>
                    <div className="status-indicator">
                      {employee.status === 'approved' && <FiCheck className="approved" />}
                      {employee.status === 'rejected' && <FiX className="rejected" />}
                      {employee.status === 'pending' && <span className="pending">Ожидает</span>}
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      ))}
    </div>
  );
};
