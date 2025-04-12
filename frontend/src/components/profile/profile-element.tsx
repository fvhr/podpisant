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

    if (loading) return <div className="profile__loading">Загрузка...</div>;
    if (!user) return <div className="profile__error">Ошибка загрузки профиля</div>;

    return (
        <div className="profile">
            <h1 className="profile__title">Профиль пользователя</h1>
            <div className="profile__container">
                <div className="profile__avatar">
                    {getInitials(user.fio)}
                </div>
                <p className="profile__title">
                    {user.fio}
                </p>
            </div>
            <div className="profile__info">
                <p><strong>Email:</strong> {user.email}</p>
                <p><strong>Телефон:</strong> {user.phone}</p>
                <p><strong>Тип уведомлений:</strong> {user.type_notification}</p>
                <p><strong>Супер админ:</strong> {user.is_super_admin ? 'Да' : 'Нет'}</p>
                <p>
                    <strong>Организации:</strong>{' '}
                    {user.user_organizations.map((org) => org.name).join(', ')}
                </p>
                <p><strong>Отделы:</strong> {(user.user_departments_ids ?? []).join(', ')}</p>
                <div className="profile__tags">
                    <strong>Теги организации:</strong>
                    <ul>
                        {Object.entries(user.organization_tags).map(([tag, details]) => (
                            <li key={tag}>
                                {tag}
                                <ul>
                                    {Object.keys(details).map(key => (
                                        <li key={key}>{key}</li>
                                    ))}
                                </ul>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
};
