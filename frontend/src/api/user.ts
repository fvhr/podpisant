import { ProfileProps } from '../types/profile.ts';
import { axiosInstance } from './instance';

export const userEmailLogin = async (email: string) => {
  try {
    const response = await axiosInstance.post('/login', { email });
    return response.data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
};

export const userCodeLogin = async (code: string, device_id: string) => {
  try {
    const response = await axiosInstance.post('/login-with-code', { code, device_id });

    if (response.status === 200) {
      return {
        success: true,
        data: response.data,
        cookiesReceived: document.cookie.includes('access_token'),
      };
    }

    return response.data;
  } catch (error) {
    console.error('Code login error:', error);
    throw error;
  }
};

export const userRefreshToken = async () => {
  try {
    const refresh = localStorage.getItem('refresh');
    const response = await axiosInstance.post(
      '/auth/user/refresh/',
      {},
      {
        headers: {
          Authorization: `Bearer ${refresh}`,
        },
      },
    );
    localStorage.setItem('access', response.data.access_token);
    return response.data;
  } catch (error) {
    console.error('Refresh error:', error);
    throw error;
  }
};

export const userInfo = async () => {
  try {
    const response = await axiosInstance.get('auth/user/info/');
    return response.data;
  } catch (error) {
    console.error('User info error:', error);
    throw error;
  }
};

export const fetchProfile = async (): Promise<ProfileProps> => {
  try {
    const response = await axiosInstance.get<ProfileProps>('/me');
    return response.data;
  } catch (error) {
    console.error('User info error:', error);
    throw error;
  }
};

export const getCurrentUserOrganizations = async () => {
  try {
    const response = await axiosInstance.get('/me');
    return response.data.user_organizations;
  } catch (error) {
    console.error('User info error:', error);
    throw error;
  }
};

export const addEmployee = async (
  fio: string,
  email: string,
  phone: string,
  type_notification: string,
  is_dep_admin: boolean,
) => {
  try {
    const response = await axiosInstance.post('/users/user/departament', {
      fio,
      email,
      phone,
      type_notification,
      is_dep_admin,
    });
    return response.data;
  } catch (error) {
    console.error('Error adding employee:', error);
    throw error;
  }
};
