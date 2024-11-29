import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

interface AuthContextType {
    isAuthenticated: boolean;
    login: () => void;
    logout: () => void;
    user: string | null;
}

export const AuthContext = createContext<AuthContextType>({
    isAuthenticated: false,
    login: () => {},
    logout: () => {},
    user: null,
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() => {
        const storedAuth = sessionStorage.getItem('isAuthenticated');
        return storedAuth === 'true';
    });    const [user, setUser] = useState<string | null>(null);
    const navigate = useNavigate();

    const login = () => {
        setIsAuthenticated(true);
        sessionStorage.setItem('isAuthenticated', 'true');
        api.get('/auth/user-info').then(response => {
            setUser(response.data.data);
        }).catch(() => {
            setUser(null);
        });
        navigate('/');
    };

    const logout = async () => {
        try {
            await api.post('/auth/logout');
            setIsAuthenticated(false);
            setUser(null);
            sessionStorage.removeItem('isAuthenticated');
            navigate('/login');
        } catch (error) {
            console.error('Failed to log out.');
        }
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout, user }}>
            {children}
        </AuthContext.Provider>
    );
};