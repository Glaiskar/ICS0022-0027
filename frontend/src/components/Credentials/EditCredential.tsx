import React, {useEffect, useRef, useState} from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import api from '../../services/api';
import { FormikHelpers } from 'formik';
import { useNavigate, useParams } from 'react-router-dom';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import { CredentialRequest } from '../../types/CredentialRequest';
import { ApiResponse } from '../../types/ApiResponse';
import { CredentialDTO } from '../../types/CredentialDTO';

const EditCredential: React.FC = () => {
    const navigate = useNavigate();
    const { id } = useParams<{ id: string }>();
    const [credential, setCredential] = useState<CredentialDTO | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [masterPassword, setMasterPassword] = useState<string | null>(null);
    const hasPrompted = useRef(false);

    const fetchCredential = async () => {
        try {
            const response = await api.post<ApiResponse<CredentialDTO>>(`/credentials/${id}`, { masterPassword });

            if (response.data.success && response.data.data) {
                setCredential(response.data.data);
                console.log(response.data.message);
            } else {
                console.error(response.data.message);
            }
        } catch (error: any) {
            console.error(error.response?.data?.message || 'Failed to fetch credential.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!id || hasPrompted.current) return;

        const promptForPassword = async () => {
            const password = prompt('Enter your master password:');
            hasPrompted.current = true; // Mark prompt as shown
            if (!password) {
                console.error('Master password is required.');
                navigate('/');
                return;
            }
            setMasterPassword(password);
        };

        promptForPassword();
    }, [id, navigate]);

    useEffect(() => {
        if (masterPassword && id) {
            fetchCredential();
        }
    }, [masterPassword, id]);


    const formik = useFormik<CredentialRequest>({
        enableReinitialize: true,
        initialValues: {
            serviceName: credential?.serviceName || '',
            serviceUsername: credential?.serviceUsername || '',
            password: '',
        },
        validationSchema: Yup.object({
            serviceName: Yup.string().required('Service name is required.'),
            serviceUsername: Yup.string().required('Service username is required.'),
            password: Yup.string().required('Password is required.'),
        }),
        onSubmit: async (values: CredentialRequest, { setSubmitting }: FormikHelpers<CredentialRequest>) => {
            try {
                const masterPassword = prompt('Enter your master password:');
                if (!masterPassword) {
                    console.error('Master password is required.');
                    return;
                }

                const body = {
                    ...values,
                    masterPassword,
                };

                const response = await api.put<ApiResponse<string>>(`/credentials/${id}`, body);

                if (response.data.success) {
                    console.log(response.data.message);
                    navigate('/');
                } else {
                    console.error(response.data.message);
                }
            } catch (error: any) {
                console.error(error.response?.data?.message || 'Failed to update credential.');
            } finally {
                setSubmitting(false);
            }
        },
    });

    if (loading) {
        return (
            <Container>
                <Box sx={{ mt: 5 }}>
                    <Typography variant="h6">Loading credential...</Typography>
                </Box>
            </Container>
        );
    }

    return (
        <Container maxWidth="sm">
            <Box sx={{ mt: 5 }}>
                <Typography variant="h4" gutterBottom>
                    Edit Credential
                </Typography>
                <form onSubmit={formik.handleSubmit} noValidate>
                    <TextField
                        fullWidth
                        id="serviceName"
                        name="serviceName"
                        label="Service Name"
                        margin="normal"
                        value={formik.values.serviceName}
                        onChange={formik.handleChange}
                        error={formik.touched.serviceName && Boolean(formik.errors.serviceName)}
                        helperText={formik.touched.serviceName && formik.errors.serviceName}
                    />
                    <TextField
                        fullWidth
                        id="serviceUsername"
                        name="serviceUsername"
                        label="Service Username"
                        margin="normal"
                        value={formik.values.serviceUsername}
                        onChange={formik.handleChange}
                        error={formik.touched.serviceUsername && Boolean(formik.errors.serviceUsername)}
                        helperText={formik.touched.serviceUsername && formik.errors.serviceUsername}
                    />
                    <TextField
                        fullWidth
                        id="password"
                        name="password"
                        label="New Password"
                        margin="normal"
                        type="password"
                        value={formik.values.password}
                        onChange={formik.handleChange}
                        error={formik.touched.password && Boolean(formik.errors.password)}
                        helperText={formik.touched.password && formik.errors.password}
                    />
                    <Button color="primary" variant="contained" fullWidth type="submit" sx={{ mt: 2 }} disabled={formik.isSubmitting}>
                        {formik.isSubmitting ? 'Updating...' : 'Update Credential'}
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default EditCredential;
