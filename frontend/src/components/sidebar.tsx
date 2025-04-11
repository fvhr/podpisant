import { Link, useLocation } from 'react-router-dom';
import { AiOutlineUser, AiOutlineSolution, AiOutlineFile } from 'react-icons/ai';
import '../styles/app.scss';
import {NavItem, SidebarProps} from "../types/navbar.ts";

export const navItems: NavItem[] = [
    { label: "Сотрудники", path: "/employees", icon: AiOutlineUser },
    { label: "Отделы", path: "/departments", icon: AiOutlineSolution },
    { label: "Документы", path: "/documents", icon: AiOutlineFile },
];

export const Sidebar = ({ isOpen }: SidebarProps) => {
    const location = useLocation();
    const isActive = (path: string) => location.pathname === path;

    return (
        <div className={`sidebar ${isOpen ? 'open' : 'closed'}`}>
            <div className="sidebar__header">
                <Link to="/" className="sidebar__logo">
                    <span className="sidebar__logo--text">НуСервисКрутой</span>
                </Link>
            </div>
            <div className="sidebar__content">
                <ul className="sidebar__menu">
                    {navItems.map((item) => (
                        <li key={item.path} className="sidebar__menu--item">
                            <Link
                                to={item.path}
                                className={`sidebar__menu--button ${isActive(item.path) ? 'active' : ''}`}
                            >
                                <item.icon className="sidebar__menu--icon" />
                                <span>{item.label}</span>
                            </Link>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};