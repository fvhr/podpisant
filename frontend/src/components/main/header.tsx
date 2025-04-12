import React from 'react';
import { FiLogOut } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';

export const Header: React.FC = () => {
    const navigate = useNavigate();
    return (
        <header className="header">
            <div className="header__logo">Подписант</div>
            <FiLogOut className="header__icon" title="Выйти" onClick={() => navigate('/login')} />
        </header>

    );
};