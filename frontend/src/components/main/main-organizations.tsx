import React, { useEffect, useState } from 'react';
import { AiFillDelete, AiFillPlusCircle } from 'react-icons/ai';
import { useNavigate } from 'react-router-dom';
import {getCurrentUserOrganizations, fetchProfile} from '../../api/user';
import { deleteOrganization } from '../../api/organization.ts';
import { MainModal } from './main-modal';
import {MainDeleteModal} from "./main-delete-modal.tsx";
import {Organization} from "../../types/organization.ts";

export const MainOrganizations: React.FC = () => {
  const navigate = useNavigate();
  const [userId, setUserId] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [organizations, setOrganizations] = useState<Organization[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [selectedOrgId, setSelectedOrgId] = useState<number | null>(null);

  useEffect(() => {
    const loadData = async () => {
      try {
        setIsLoading(true);

        const [orgs, profile] = await Promise.all([
          getCurrentUserOrganizations(),
          fetchProfile(),
        ]);

        setOrganizations(orgs || []);
        setUserId(profile.id);
      } catch (err) {
        console.error('Ошибка при загрузке данных:', err);
        setError('Не удалось загрузить данные');
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, []);

  const refreshOrganizations = async () => {
    try {
      setIsLoading(true);
      const orgs = await getCurrentUserOrganizations();
      setOrganizations(orgs || []);
    } catch (err) {
      console.error('Ошибка при обновлении организаций:', err);
      setError('Не удалось обновить список организаций');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedOrgId) return;

    try {
      await deleteOrganization(selectedOrgId);
      setOrganizations((prev) => prev.filter((org) => org.id !== selectedOrgId));
      setShowModal(false);
      setSelectedOrgId(null);
    } catch (error) {
      console.error('Ошибка при удалении:', error);
    }
  };

  const handleOrganizationClick = (orgId: number) => {
    navigate(`/documents/${orgId}`);
  };

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
                    <div
                        className="organization-actions"
                        onClick={() => {
                          setSelectedOrgId(org.id);
                          setShowModal(true);
                        }}
                    >
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

        {showModal && (
            <MainDeleteModal onClose={() => setShowModal(false)} onConfirm={handleDelete} />
        )}

        {isModalOpen && userId && (
            <MainModal
                onClose={() => setIsModalOpen(false)}
                onSuccess={refreshOrganizations}
                admin_id='9fcc954f-dec7-45d7-911f-c8394c73e5d6'
            />
        )}
      </main>
  );
};