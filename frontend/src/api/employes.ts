import { axiosInstance } from './instance';

export const getAllEmployes = async (id: number) => {
	try {
		const response = await axiosInstance.get(`/organizations/${id}/users`);
		return response.data;
	} catch (error) {
		console.error('User info error:', error);
		throw error;
	}
}

export const deleteUserFromDepartment = async (userUuid: string, depId: number) => {
	try {
		const response = await axiosInstance.delete(`/users/${userUuid}/${depId}`);
		return response.data;
	} catch (error) {
		console.error('Error deleting user from department:', error);
		throw error;
	}
};