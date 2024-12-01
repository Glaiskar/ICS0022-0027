import React from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import api from '../../services/api';
import { FormikHelpers } from 'formik';
import { useNavigate } from 'react-router-dom';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import { CredentialRequest } from '../../types/CredentialRequest';
import { ApiResponse } from '../../types/ApiResponse';
import DOMPurify from "dompurify";

const AddCredential: React.FC = () => {
    const navigate = useNavigate();

    const formik = useFormik<CredentialRequest>({
        initialValues: {
            serviceName: '',
            serviceUsername: '',
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

                for (const key in values) {
                    if (values.hasOwnProperty(key)) {
                        values[key as keyof CredentialRequest] = DOMPurify.sanitize(values[key as keyof CredentialRequest]);
                    }
                }

                const body = {
                    ...values,
                    masterPassword,
                };

                const response = await api.post<ApiResponse<string>>('/credentials', body);

                if (response.data.success) {
                    console.log(response.data.message);
                    navigate('/');
                } else {
                    console.error(response.data.message);
                }
            } catch (error: any) {
                console.error(error.response?.data?.message || 'Failed to add credential.');
            } finally {
                setSubmitting(false);
            }
        },
    });

    return (
        <Container maxWidth="sm">
            <Box sx={{ mt: 5 }}>
                <Typography variant="h4" gutterBottom>
                    Add Credential
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
                        label="Password"
                        margin="normal"
                        type="password"
                        value={formik.values.password}
                        onChange={formik.handleChange}
                        error={formik.touched.password && Boolean(formik.errors.password)}
                        helperText={formik.touched.password && formik.errors.password}
                    />
                    <Button color="primary" variant="contained" fullWidth type="submit" sx={{ mt: 2 }} disabled={formik.isSubmitting}>
                        {formik.isSubmitting ? 'Adding...' : 'Add Credential'}
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default AddCredential;
