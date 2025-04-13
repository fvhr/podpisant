import { axiosInstance } from './instance';

export const addStage = async (
  id: number,
  name: string,
  number: number,
  deadline: Date,
  user_ids: string[],
) => {
  const data = { id, name, number, deadline, user_ids };
  try {
    const response = await axiosInstance.post(`/documents/${id}/stages`, [data]);
    return response.data;
  } catch (error) {
    console.error('Ошибка при создании этапа:', error);
    throw error;
  }
};
