import axios from 'axios';

export const axiosInstance = axios.create({
  baseURL: 'https://menoitami.ru/api',
  withCredentials: true,
});
