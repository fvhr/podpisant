import { AiOutlineFile, AiOutlineSolution, AiOutlineUser } from 'react-icons/ai';
import { Link, useLocation } from 'react-router-dom';
import '../styles/app.scss';
import { NavItem, SidebarProps } from '../types/navbar.ts';

export const navItems: NavItem[] = [
  { label: 'Сотрудники', path: '/employees', icon: AiOutlineUser },
  { label: 'Отделы', path: '/departments', icon: AiOutlineSolution },
  { label: 'Документы', path: '/documents', icon: AiOutlineFile },
];

export const Sidebar = ({ isOpen, toggleSidebar }: SidebarProps) => {
  const location = useLocation();
  const isActive = (path: string) => location.pathname === path;

  const handleSidebarClick = (e: React.MouseEvent) => {
    const target = e.target as HTMLElement;

    if (target.closest('a') || target.closest('button')) {
      return;
    }

    toggleSidebar?.();
  };

  return (
    <div className={`sidebar ${isOpen ? 'open' : 'closed'}`} onClick={handleSidebarClick}>
      <div className="sidebar__header">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="28"
          height="28"
          viewBox="0 0 24 24"
          fill="none"
          stroke="#f0f0ff"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          className="lucide lucide-file-cog-icon lucide-file-cog">
          <path d="M14 2v4a2 2 0 0 0 2 2h4" />
          <path d="m3.2 12.9-.9-.4" />
          <path d="m3.2 15.1-.9.4" />
          <path d="M4.677 21.5a2 2 0 0 0 1.313.5H18a2 2 0 0 0 2-2V7l-5-5H6a2 2 0 0 0-2 2v2.5" />
          <path d="m4.9 11.2-.4-.9" />
          <path d="m4.9 16.8-.4.9" />
          <path d="m7.5 10.3-.4.9" />
          <path d="m7.5 17.7-.4-.9" />
          <path d="m9.7 12.5-.9.4" />
          <path d="m9.7 15.5-.9-.4" />
          <circle cx="6" cy="14" r="3" />
        </svg>
        {isOpen && (
          <div className="sidebar__logo">
            <span className="sidebar__logo--text">Подписант</span>
          </div>
        )}
      </div>
      <div className="sidebar__content">
        <ul className="sidebar__menu">
          {navItems.map((item) => (
            <li key={item.path} className="sidebar__menu--item">
              <Link
                to={item.path}
                className={`sidebar__menu--button ${isActive(item.path) ? 'active' : ''}`}
                onClick={(e) => e.stopPropagation()}>
                <div className="sidebar__icon-container">
                  <item.icon className="sidebar__menu--icon" />
                  {!isOpen && <span className="sidebar__icon-label">{item.label}</span>}
                </div>
                {isOpen && <span className="sidebar__menu--text">{item.label}</span>}
              </Link>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};
