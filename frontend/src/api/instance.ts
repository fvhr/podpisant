import axios from 'axios';

export const axiosInstance = axios.create({
  baseURL: 'https://menoitami.ru/api',
  withCredentials: true,
});

axiosInstance.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 500) {
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);