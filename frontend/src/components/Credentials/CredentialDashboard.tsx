import React, {useEffect, useRef, useState} from 'react';
import api from '../../services/api';
import { CredentialDTO } from '../../types/CredentialDTO';
import { ApiResponse } from '../../types/ApiResponse';
import { useNavigate } from 'react-router-dom';
import {
    Container,
    Typography,
    Box,
    TableContainer,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    Paper,
    Button,
    IconButton,
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { toast } from 'react-toastify';

const CredentialDashboard: React.FC = () => {
    const [credentials, setCredentials] = useState<CredentialDTO[] | null>(null);
    const [visiblePasswords, setVisiblePasswords] = useState<{ [key: number]: boolean }>({});
    const navigate = useNavigate();
    const [masterPassword, setMasterPassword] = useState<string | null>(null);
    const hasPrompted = useRef(false); // Track if the prompt has been shown

    const fetchCredentials = async () => {
        try {
            const response = await api.post<ApiResponse<CredentialDTO[]>>('/credentials/all', { masterPassword});
            if (response.data.success && response.data.data) {
                setCredentials(response.data.data);
                console.log(response.data.message);
            } else {
                console.error(response.data.message);
            }
        } catch (error: any) {
            console.error(error.response?.data?.message || 'Failed to fetch credentials.');
        }
    };

    useEffect(() => {
        if (hasPrompted.current) return;
        const promptForPassword = async () => {
            const password = prompt('Enter your master password:');
            hasPrompted.current = true;
            if (!password) {
                console.error('Master password is required to view credentials.');
                return;
            }
            setMasterPassword(password);
        };

        promptForPassword();
    }, []);

    useEffect(() => {
        if (masterPassword) {
            fetchCredentials();
        }
    }, [masterPassword]);


    const handleDelete = async (id: number) => {
        if (!window.confirm('Are you sure you want to delete this credential?')) return;

        try {
            const masterPassword = prompt('Enter your master password:');
            if (!masterPassword) {
                toast.error('Master password is required to delete credentials.', {icon: false});
                return;
            }

            const response = await api.delete<ApiResponse<string>>(`/credentials/${id}`, {
                data: { masterPassword: masterPassword },
            });

            if (response.data.success) {
                console.log(response.data.message);
                setCredentials(credentials!.filter((cred) => cred.id !== id));
            } else {
                console.error(response.data.message);
            }
        } catch (error: any) {
            console.error(error.response?.data?.message || 'Failed to delete credential.');        }
    };

    const togglePasswordVisibility = (id: number) => {
        setVisiblePasswords((prevState) => ({
            ...prevState,
            [id]: !prevState[id],
        }));
    };

    return (
        <Container>
            <Box sx={{ mt: 5, mb: 2, display: 'flex', justifyContent: 'space-between' }}>
                <Typography variant="h4">Your Credentials</Typography>
                <Button variant="contained" color="primary" onClick={() => navigate('/credentials/add')}>
                    Add Credential
                </Button>
            </Box>
            <TableContainer component={Paper}>
                <Table aria-label="credentials table">
                    <TableHead>
                        <TableRow>
                            <TableCell>Service Name</TableCell>
                            <TableCell>Service Username</TableCell>
                            <TableCell>Decrypted Password</TableCell>
                            <TableCell align="right">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    {credentials && (
                        <TableBody>
                            {credentials!.map((credential) => (
                                <TableRow key={credential.id}>
                            <TableCell>{credential.serviceName}</TableCell>
                            <TableCell>{credential.serviceUsername}</TableCell>
                            <TableCell>
                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                    {visiblePasswords[credential.id] ? credential.decryptedPassword : '••••••••'}
                                    <Button
                                        size="small"
                                        onClick={() => togglePasswordVisibility(credential.id)}
                                        sx={{ ml: 1 }}
                                    >
                                        {visiblePasswords[credential.id] ? 'Hide' : 'Show'}
                                    </Button>
                                    <Button
                                        size="small"
                                        onClick={() => {
                                            navigator.clipboard.writeText(credential.decryptedPassword);
                                        }}
                                        sx={{ ml: 1 }}
                                    >
                                        Copy
                                    </Button>
                                </Box>
                            </TableCell>
                            <TableCell align="right">
                                <IconButton color="primary" onClick={() => navigate(`/credentials/edit/${credential.id}`)}>
                                    <Edit />
                                </IconButton>
                                <IconButton color="secondary" onClick={() => handleDelete(credential.id)}>
                                    <Delete />
                                </IconButton>
                            </TableCell>
                        </TableRow>
                        ))}
                        {credentials!.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={4} align="center">
                                    No credentials found.
                                </TableCell>
                            </TableRow>
                        )}
                        </TableBody>
                    )}
                </Table>
            </TableContainer>
        </Container>
    );
};

export default CredentialDashboard;
