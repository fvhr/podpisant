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
