import React, { useEffect, useState } from 'react';
import { AiFillDelete, AiFillPlusCircle } from 'react-icons/ai';
import { useNavigate } from 'react-router-dom';
import { getCurrentUserOrganizations } from '../../api/user';
import { MainModal } from './main-modal';

interface Organization {
  id: number;
  name: string;
  description: string;
}

export const MainOrganizations: React.FC = () => {
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [organizations, setOrganizations] = useState<Organization[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchOrganizations = async () => {
      try {
        setIsLoading(true);
        const userOrganizations = await getCurrentUserOrganizations();
        setOrganizations(userOrganizations || []);
      } catch (err) {
        console.error('Ошибка при загрузке организаций:', err);
        setError('Не удалось загрузить организации');
      } finally {
        setIsLoading(false);
      }
    };

    fetchOrganizations();
  }, []);

  const handleOrganizationClick = (orgId: number) => {
    navigate(`/documents/${orgId}`); 
  };

  console.log(organizations);

  if (isLoading) {
    return <div className="loading-message">Загрузка организаций...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  return (
    <main className="organizations-container">
      <div className="organizations-header">
        <button className="add-organization-button" onClick={() => setIsModalOpen(true)}>
          <AiFillPlusCircle className="button-icon" />
          Добавить организацию
        </button>
        {error && <div className="error-message">{error}</div>}
      </div>

      <div className="organizations-list">
        {organizations.length > 0 ? (
          organizations.map((org) => (
            <div key={org.id} className="organization-card">
              <div className="organization-content" onClick={() => handleOrganizationClick(org.id)}>
                <h3 className="organization-name">{org.name}</h3>
                <p className="organization-description">{org.description}</p>
              </div>
              <div className="organization-actions">
                <AiFillDelete className="delete-icon" />
              </div>
            </div>
          ))
        ) : (
          <div className="no-organizations-message">
            У вас пока нет организаций. Создайте первую!
          </div>
        )}
      </div>

      {isModalOpen && <MainModal onClose={() => setIsModalOpen(false)} />}
    </main>
  );
};
