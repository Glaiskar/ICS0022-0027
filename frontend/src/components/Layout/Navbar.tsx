import React, {useContext} from "react";
import {AuthContext} from "../../contexts/AuthContext";
import {AppBar, Box, Button, Toolbar, Typography} from "@mui/material";
import {Link} from "react-router-dom";

const Navbar: React.FC = () => {
    const { isAuthenticated, logout, user } = useContext(AuthContext);

    return (
        <Box sx={{ flexGrow: 1, marginBottom: '20px' }}>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        SecureWebPassManager
                    </Typography>
                    {isAuthenticated ? (
                        <>
                            <Typography variant="body1" sx={{ marginRight: 2 }}>
                                Welcome, {user}
                            </Typography>
                            <Button color="inherit" component={Link} to="/">
                                Dashboard
                            </Button>
                            <Button color="inherit" onClick={logout}>
                                Logout
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button color="inherit" component={Link} to="/login">
                                Login
                            </Button>
                            <Button color="inherit" component={Link} to="/register">
                                Register
                            </Button>
                        </>
                    )}
                </Toolbar>
            </AppBar>
        </Box>
    );
};

export default Navbar;