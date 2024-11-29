import axios from "axios";
import {toast} from "react-toastify";

axios.defaults.withCredentials = true;

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
    headers: {
        'Content-Type': "application/json",
    },
    withCredentials: true,
});

api.interceptors.request.use(async (config) => {
    const excludedPaths = ['/register', '/login', '/verify-otp'];
    if (config['url'] && excludedPaths.some(path => config['url']?.includes(path))) {
        return config; // Skip token refresh
    }
    if (!config.headers.Authorization) {
        const response = await axios.get('/api/auth/refresh-token', {
            withCredentials: true,
        });

        const token = response.data.token;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            const { status, data } = error.response;

            if (status === 401) {
                window.location.href = '/login';
                toast.error('Session expired. Please log in again.');
            } else if (status === 403) {
                toast.error(data.message || 'Forbidden');
            } else if (status >= 500) {
                toast.error('Server error. Please try again later.');
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
