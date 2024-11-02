import axios from "axios";
import {getToken, clearToken} from "../utils/tokenUtils";
import {toast} from "react-toastify";

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
    headers: {
        'Content-Type': "application/json",
    },
});

api.interceptors.request.use(
    (config) => {
        const token = getToken();
        if (token && config.headers) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            const { status, data } = error.response;

            if (status === 401) {
                clearToken();
                window.location.href = '/login';
                toast.error('Session expired. Please log in again.');
            } else if (status === 403) {
                toast.error('Forbidden');
            } else {
                toast.error(data.message || 'An error occurred. Please try again.');
            }
        } else if (error.request) {
            toast.error('No response from server. Please try again later.');
        } else {
            toast.error(error.message);
        }

        return Promise.reject(error);
    }
);

export default api;
