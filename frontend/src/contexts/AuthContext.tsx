import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { getToken, setToken, clearToken } from '../utils/tokenUtils';
import jwtDecode from 'jwt-decode';
import { useNavigate } from 'react-router-dom';
import {JwtPayload} from "../types/JwtPayload";

interface AuthContextType {
    token: string | null;
    isAuthenticated: boolean;
    login: (token: string) => void;
    logout: () => void;
    user: JwtPayload | null;
}

export const AuthContext = createContext<AuthContextType>({
    token: null,
    isAuthenticated: false,
    login: () => {},
    logout: () => {},
    user: null,
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [authToken, setAuthToken] = useState<string | null>(getToken());
    const [user, setUser] = useState<JwtPayload | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (authToken) {
            try {
                const decoded = jwtDecode<JwtPayload>(authToken);
                setUser(decoded);
            } catch (error) {
                console.error('Invalid token:', error)
                logout();
            }
        } else {
            setUser(null);
        }
    }, [authToken]);

    const login = (newToken: string) => {
        setAuthToken(newToken);
        setToken(newToken);
        try {
            const decoded = jwtDecode<JwtPayload>(newToken);
            setUser(decoded);
        } catch (error) {
            console.error('Invalid token:', error);
            logout();
        }
    };

    const logout = () => {
        clearToken();
        setAuthToken(null);
        setUser(null);
        navigate('/login')
    };

    const isAuthenticated = !!authToken;

    return (
        <AuthContext.Provider value={{ token: authToken, isAuthenticated, login, logout, user }}>
            {children}
        </AuthContext.Provider>
    );
};