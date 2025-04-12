import axios from 'axios';

export const axiosInstanсe = axios.create({
    baseURL: 'https://menoitami.ru/api',
});

axiosInstanсe.interceptors.request.use(
    (config) => {
        const accessToken = localStorage.getItem('access');
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    },
);