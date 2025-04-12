type ReactIconType = React.FC<React.SVGProps<SVGSVGElement>>;

export type NavItem = {
  label: string;
  path: string;
  icon: ReactIconType;
};

export interface SidebarProps {
  isOpen: boolean;
  toggleSidebar: () => void;
}
