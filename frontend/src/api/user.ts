import axios from 'axios';
import { axiosInstanсe } from './instance';

export const userEmailLogin = async (email: string) => {
    const data = { email };
    try {
        const response = await axiosInstanсe.post('/login', data);
        return response.data;
    } catch (error) {
        console.log('Ошибка', error);
        throw error;
    }
};

export const userCodeLogin = async (code: string, device_id: string) => {
    const data = { code, device_id };
    try {
        const response = await axiosInstanсe.post('/login-with-code', data);
        return response;
    } catch (error) {
        console.log('Ошибка', error);
    }
};

export const userRefreshToken = async () => {
    const refresh = localStorage.getItem('refresh');
    try {
        const response = await axios.post('https://task_manager_api.cl.ru.net/auth/user/refresh/', null, {
            headers: {
                Authorization: `Bearer ${refresh}`
            }
        });
        localStorage.setItem('access', response.data.access_token);
        return response;
    } catch (error) {
        console.log('Ошибка', error);
    }
};

export const userInfo = async () => {
    try {
        const response = await axiosInstanсe.get('auth/user/info/');
        return response.data;
    } catch (error) {
        console.log('Ошибка', error);
    }
};