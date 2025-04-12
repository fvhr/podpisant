import { axiosInstance } from './instance';

export const getDocumentsCurrentOrganizations = async (id: number) => {
  try {
    const response = await axiosInstance.get(`/documents/${id}`);
    return response.data;
  } catch (error) {
    console.error('Documents error:', error);
    throw error;
  }
};

interface CreateDocumentParams {
  name: string;
  organization_id: number;
  file: File;
}

export const createDocument = async ({ name, organization_id, file }: CreateDocumentParams) => {
  const formData = new FormData();

  formData.append('file', file);

  const itemJson = JSON.stringify({ name, organization_id });
  formData.append('item_json', itemJson);

  try {
    const response = await axiosInstance.post('/documents', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  } catch (error) {
    console.error('Ошибка при создании документа:', error);
    throw error;
  }
};
