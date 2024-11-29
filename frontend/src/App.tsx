import React, {useEffect, useRef} from 'react';
import logo from './logo.svg';
import './App.css';
import {AuthProvider} from "./contexts/AuthContext";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import {ToastContainer} from "react-toastify";
import ProtectedRoute from "./components/Layout/ProtectedRoute";
import Navbar from "./components/Layout/Navbar";
import EditCredential from "./components/Credentials/EditCredential";
import AddCredential from "./components/Credentials/AddCredential";
import OtpVerification from "./components/Auth/OtpVerification";
import Login from "./components/Auth/Login";
import Register from "./components/Auth/Register";
import CredentialDashboard from "./components/Credentials/CredentialDashboard";

const App: React.FC = () => {
  return (
      <Router>
        <AuthProvider>
          <Navbar />
          <Routes>
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/verify-otp" element={<OtpVerification />} />

            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <CredentialDashboard />
                </ProtectedRoute>
              }
            />
            <Route
                path="/credentials/add"
                element={
                  <ProtectedRoute>
                    <AddCredential />
                  </ProtectedRoute>
                }
            />
            <Route
                path="/credentials/edit/:id"
                element={
                  <ProtectedRoute>
                    <EditCredential />
                  </ProtectedRoute>
                }
            />
          </Routes>
        </AuthProvider>
      </Router>
  )
}

export default App;
