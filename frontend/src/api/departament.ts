import { axiosInstance } from './instance';

export const getAllDepartaments = async (id: number) => {
  try {
    const response = await axiosInstance.get(`/departments/${id}`);
    return response.data;
  } catch (error) {
    console.error('User info error:', error);
    throw error;
  }
};

export const createDepartment = async (org_id: number, name: string, description: string) => {
  try {
    const response = await axiosInstance.post(`/departments/${org_id}`, {
      name,
      description,
    });
    return response.data;
  } catch (error) {
    console.error('Ошибка при создании отдела:', error);
    throw error;
  }
};
