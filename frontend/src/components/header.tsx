import React from 'react';
import '../styles/app.scss';
import { FiLogOut } from 'react-icons/fi';

export const Header: React.FC = () => {
    return (
        <header className="header">
            <div className="header__logo">НуСервисКрутой</div>
            <FiLogOut className="header__icon" title="Выйти" />
        </header>
    );
};