import React from 'react';
import { FiLogOut } from 'react-icons/fi';
import '../styles/app.scss';

interface HeaderProps {
    isSidebarOpen: boolean;
    toggleSidebar: () => void;
}

export const Header: React.FC<HeaderProps> = () => {
    return (
        <header className="header">
            <div className="header__logo">НуСервисКрутой</div>
            <FiLogOut className="header__icon" title="Выйти" />
        </header>
    );
};