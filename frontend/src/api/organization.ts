import { AddOrganizationPayload } from '../types/organization.ts';
import { axiosInstance } from './instance.ts';

export const deleteOrganization = async (organization_id: number) => {
  try {
    const response = await axiosInstance.delete(`/organizations/${organization_id}`);
    return response.data;
  } catch (error) {
    console.error('User info error:', error);
    throw error;
  }
};

export const addOrganization = async (data: AddOrganizationPayload) => {
  try {
    const response = await axiosInstance.post('/organizations', data);
    return response.data;
  } catch (error) {
    console.error('Ошибка при создании организации:', error);
    throw error;
  }
};
