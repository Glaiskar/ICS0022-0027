import React from "react";
import {useNavigate} from "react-router-dom";
import * as Yup from 'yup';
import {FormikHelpers, useFormik} from "formik";
import api from "../../services/api";
import {ApiResponse} from "../../types/ApiResponse";
import {Box, Button, Container, TextField, Typography} from "@mui/material";
import {RegisterRequest} from "../../types/RegisterRequest";
import DOMPurify from "dompurify";

const Register: React.FC = () => {
    const navigate = useNavigate();

    const formik = useFormik<RegisterRequest>({
        initialValues: {
            username: '',
            email: '',
            password: '',
            masterPassword: '',
        },
        validationSchema: Yup.object({
            username: Yup.string().required('Username is required.'),
            email: Yup.string().email('Invalid email address.').required('Email is required.'),
            password: Yup.string().min(6, 'Password must be at least 6 characters.').required('Password is required.'),
            masterPassword: Yup.string()
                .min(6, 'Master Password must be at least 6 characters.')
                .required('Master Password is required.'),
        }),
        onSubmit: async (values: RegisterRequest, { setSubmitting }: FormikHelpers<RegisterRequest>) => {
            try {
                for (const key in values) {
                    if (values.hasOwnProperty(key)) {
                        values[key as keyof RegisterRequest] = DOMPurify.sanitize(values[key as keyof RegisterRequest]);
                    }
                }
                const response = await api.post<ApiResponse<string>>('/auth/register', values);
                if (response.data.success) {
                    console.log(response.data.message);
                    navigate('/login');
                } else {
                    console.error(response.data.message);
                }
            } catch (error: any) {
                console.error(error.response?.data?.message || 'Registration failed.');
            } finally {
                setSubmitting(false);
            }
        }
    });

    return (
        <Container maxWidth="sm">
            <Box sx={{ mt: 5 }}>
                <Typography variant="h4" gutterBottom>
                    Register
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
                        id="email"
                        name="email"
                        label="Email"
                        margin="normal"
                        type="email"
                        value={formik.values.email}
                        onChange={formik.handleChange}
                        error={formik.touched.email && Boolean(formik.errors.email)}
                        helperText={formik.touched.email && formik.errors.email}
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
                    <TextField
                        fullWidth
                        id="masterPassword"
                        name="masterPassword"
                        label="Master Password"
                        margin="normal"
                        type="password"
                        value={formik.values.masterPassword}
                        onChange={formik.handleChange}
                        error={formik.touched.masterPassword && Boolean(formik.errors.masterPassword)}
                        helperText={formik.touched.masterPassword && formik.errors.masterPassword}
                    />
                    <Button color="primary" variant="contained" fullWidth type="submit" sx={{ mt: 2 }} disabled={formik.isSubmitting}>
                        {formik.isSubmitting ? 'Registering...' : 'Register'}
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default Register;