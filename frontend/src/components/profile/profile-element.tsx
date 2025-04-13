import React, { useEffect, useState } from 'react';
import { fetchProfile } from '../../api/user';
import { ProfileProps } from '../../types/profile.ts';

export const ProfileElement: React.FC = () => {
  const [user, setUser] = useState<ProfileProps | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchProfile()
      .then(setUser)
      .finally(() => setLoading(false));
  }, []);

  const getInitials = (fullName: string) => {
    const parts = fullName.trim().split(' ');
    const initials = parts[0]?.[0] + (parts[1]?.[0] ?? '');
    return initials.toUpperCase();
  };

  if (loading) return <div className="profile-loading">Загрузка профиля...</div>;
  if (!user) return <div className="profile-error">Ошибка загрузки профиля</div>;

  return (
    <div className="profile-page">
      <div className="profile-header">
        <div className="profile-avatar">{getInitials(user.fio)}</div>
        <div className="profile-title">
          <h1>{user.fio}</h1>
          <p className="profile-subtitle">Личный профиль</p>
        </div>
      </div>

      <div className="profile-content">
        <div className="profile-section">
          <h2>Контактная информация</h2>
          <div className="profile-info-grid">
            <div>
              <label>Email</label>
              <p>{user.email}</p>
            </div>
            <div>
              <label>Телефон</label>
              <p>{user.phone || 'Не указан'}</p>
            </div>
          </div>
        </div>

        <div className="profile-section">
          <h2>Настройки</h2>
          <div className="profile-info-grid">
            <div>
              <label>Вид предпочтительной связи</label>
              <p>{user.type_notification}</p>
            </div>
            <div>
              <label>Роль</label>
              <p>{user.is_super_admin ? 'Супер администратор' : 'Пользователь'}</p>
            </div>
          </div>
        </div>

        <div className="profile-section">
          <h2>Организации</h2>
          <div className="organizations-list">
            {user.user_organizations.map((org) => (
              <div key={org.id} className="organization-item">
                <h3>{org.name}</h3>
                {org.description && <p>{org.description}</p>}
              </div>
            ))}
          </div>
        </div>

        {Object.keys(user.organization_tags).length > 0 && (
          <div className="profile-section">
            <h2>Теги организаций</h2>
            <div className="tags-grid">
              {Object.entries(user.organization_tags).map(([tag, details]) => (
                <div key={tag} className="tag-item">
                  <h3>{tag}</h3>
                  <ul>
                    {Object.keys(details).map((key) => (
                      <li key={key}>{key}</li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
