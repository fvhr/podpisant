import { axiosInstance } from './instance';

export const getAllDocuments = async () => {
  try {
    const response = await axiosInstance.get('/documents');
    return response.data;
  } catch (error) {
    console.error('Documents error:', error);
    throw error;
  }
};
