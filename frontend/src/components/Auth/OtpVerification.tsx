import React, { useContext } from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import api from '../../services/api';
import { useLocation } from 'react-router-dom';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import { AuthContext } from '../../contexts/AuthContext';
import DOMPurify from "dompurify";

interface LocationState {
    username: string;
}

const OtpVerification: React.FC = () => {
    const location = useLocation();
    const { login } = useContext(AuthContext);
    const { username } = (location.state || {}) as LocationState;

    const formik = useFormik({
        initialValues: {
            otp: '',
        },
        validationSchema: Yup.object({
            otp: Yup.string().required('OTP is required'),
        }),
        onSubmit: async (values, { setSubmitting }) => {
            try {
                const body = {
                    username: username,
                    otp: DOMPurify.sanitize(values.otp),
                };

                const response = await api.post('/auth/verify-otp', body);

                if (response.data.success) {
                    console.log(response.data.message);
                    login();
                } else {
                    console.error(response.data.message);
                }
            } catch (error: any) {
                console.error(error.response?.data?.message || 'OTP verification failed.');
            } finally {
                setSubmitting(false);
            }
        },
    });

    return (
        <Container maxWidth="sm">
            <Box sx={{ mt: 5 }}>
                <Typography variant="h4" gutterBottom>
                    OTP Verification
                </Typography>
                <form onSubmit={formik.handleSubmit} noValidate>
                    <TextField
                        fullWidth
                        id="otp"
                        name="otp"
                        label="OTP"
                        margin="normal"
                        value={formik.values.otp}
                        onChange={formik.handleChange}
                        error={formik.touched.otp && Boolean(formik.errors.otp)}
                        helperText={formik.touched.otp && formik.errors.otp}
                    />
                    <Button color="primary" variant="contained" fullWidth type="submit" sx={{ mt: 2 }} disabled={formik.isSubmitting}>
                        {formik.isSubmitting ? 'Verifying...' : 'Verify OTP'}
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default OtpVerification;
