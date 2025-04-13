import { axiosInstance } from './instance';

export const getDocumentsCurrentOrganizations = async (id: number) => {
  try {
    const response = await axiosInstance.get(`/documents/organizations/${id}`);
    return response.data;
  } catch (error) {
    console.error('Documents error:', error);
    throw error;
  }
};

export const getDocumentInfo = async (id: number) => {
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

export const getDocumentOrganization = async (id: number) => {
  try {
    const response = await axiosInstance.get(`/documents/${id}/stages`);
    return response.data;
  } catch (error) {
    console.error('Documents error:', error);
    throw error;
  }
};

export const signatureDocument = async (document_id: number, stage_id: number) => {
  try {
    const response = await axiosInstance.post(`/documents/${document_id}/stages/${stage_id}/sign`);
    return response.data;
  } catch (error) {
    console.error('Documents error:', error);
    throw error;
  }
};

export const rejectDocument = async (document_id: number, stage_id: number) => {
  try {
    const response = await axiosInstance.post(`/documents/${document_id}/stages/${stage_id}/reject`);
    return response.data;
  } catch (error) {
    console.error('Documents error:', error);
    throw error;
  }
};
