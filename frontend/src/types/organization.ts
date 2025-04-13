export interface Organization {
    id: number;
    name: string;
    description: string;
}

export interface AddOrganizationPayload {
    name: string,
    description: string,
    admin_id?: string,
}