import { axiosInstance } from './instance';

export const getAllEmployes = async (id: number) => {
  try {
    const response = await axiosInstance.get(`/organizations/${id}/users`);
    return response.data;
  } catch (error) {
    console.error('User info error:', error);
    throw error;
  }
};
