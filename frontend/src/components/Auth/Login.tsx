import React, {useContext, useEffect} from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import api from '../../services/api';
import { FormikHelpers } from 'formik';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import {AuthenticationRequest} from "../../types/AuthenticationRequest";

const Login: React.FC = () => {
    const navigate = useNavigate();
    const { login } = React.useContext(AuthContext);

    const formik = useFormik<AuthenticationRequest>({
        initialValues: {
            username: '',
            password: '',
        },
        validationSchema: Yup.object({
            username: Yup.string().required('Username is required.'),
            password: Yup.string().required('Password is required.'),
        }),
        onSubmit: async (values: AuthenticationRequest, { setSubmitting }: FormikHelpers<AuthenticationRequest>) => {
            try {
                const response = await api.post('/auth/login', values);
                if (response.data.success) {
                    console.log(response.data.message);
                    navigate('/verify-otp', { state: { username: values.username } });
                } else {
                    console.error(response.data.message);
                }
            } catch (error: any) {
                console.error(error.response?.data?.message || 'Login failed.');
            } finally {
                setSubmitting(false);
            }
        },
    });

    return (
        <Container maxWidth="sm">
            <Box sx={{ mt: 5 }}>
                <Typography variant="h4" gutterBottom>
                    Login
                </Typography>
                <form onSubmit={formik.handleSubmit} noValidate>
                    <TextField
                        fullWidth
                        id="username"
                        name="username"
                        label="Username"
                        margin="normal"
                        value={formik.values.username}
                        onChange={formik.handleChange}
                        error={formik.touched.username && Boolean(formik.errors.username)}
                        helperText={formik.touched.username && formik.errors.username}
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
                        {formik.isSubmitting ? 'Logging in...' : 'Login'}
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default Login;
