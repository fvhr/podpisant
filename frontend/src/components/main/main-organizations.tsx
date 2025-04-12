import React, { useState } from 'react';
import { AiFillDelete, AiFillPlusCircle } from 'react-icons/ai';
import { useNavigate } from 'react-router-dom';
import { MainModal } from './main-modal';

interface Organization {
  id: number;
  name: string;
  description: string;
}

export const MainOrganizations: React.FC = () => {
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [organizations, setOrganizations] = useState<Organization[]>([
    {
      id: 1,
      name: 'ТехноПром',
      description: 'Производство промышленного оборудования',
    },
    {
      id: 2,
      name: 'СтройГарант',
      description: 'Строительная компания',
    },
    {
      id: 3,
      name: 'АгроПродукт',
      description: 'Сельскохозяйственное предприятие',
    },
    {
      id: 4,
      name: 'МедТехнологии',
      description: 'Производство медицинского оборудования',
    },
    {
      id: 5,
      name: 'ЭкоЭнергия',
      description: 'Возобновляемые источники энергии',
    },
  ]);

  const handleOrganizationClick = (id: number) => {
    navigate(`/organization/${id}`);
  };

  const handleDeleteClick = (id: number, e: React.MouseEvent) => {
    e.stopPropagation();
    setOrganizations((orgs) => orgs.filter((org) => org.id !== id));
  };

  const handleCreateOrganization = (name: string, description: string) => {
    const newOrganization = {
      id: Math.max(0, ...organizations.map((org) => org.id)) + 1,
      name,
      description,
    };
    setOrganizations((orgs) => [...orgs, newOrganization]);
  };

  return (
    <main className="organizations-container">
      <div className="organizations-header">
        <button className="add-organization-button" onClick={() => setIsModalOpen(true)}>
          <AiFillPlusCircle className="button-icon" />
          Добавить организацию
        </button>
      </div>

      <div className="organizations-list">
        {organizations.map((org) => (
          <div key={org.id} className="organization-card">
            <div className="organization-content" onClick={() => handleOrganizationClick(org.id)}>
              <h3 className="organization-name">{org.name}</h3>
              <p className="organization-description">{org.description}</p>
            </div>
            <div className="organization-actions" onClick={(e) => handleDeleteClick(org.id, e)}>
              <AiFillDelete className="delete-icon" />
            </div>
          </div>
        ))}
      </div>

      {isModalOpen && (
        <MainModal onClose={() => setIsModalOpen(false)} onCreate={handleCreateOrganization} />
      )}
    </main>
  );
};
