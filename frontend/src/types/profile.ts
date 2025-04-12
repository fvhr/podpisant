export type Organization = {
    id: number;
    name: string;
    description: string;
};

export interface ProfileProps {
    id: string;
    email: string;
    fio: string;
    phone: string;
    is_super_admin: boolean;
    type_notification: 'TG' | 'EMAIL' | 'PHONE';
    admin_in_organization: number;
    user_organizations: Organization[];
    user_departments_ids: number[];
    organization_tags: Record<string, Record<string, any>>;
}