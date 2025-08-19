import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

function OtpVerification() {
    const [otpCode, setOtpCode] = useState('');
    const [loading, setLoading] = useState(false);
    const [resendLoading, setResendLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [timeLeft, setTimeLeft] = useState(300); // 5 minutes
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { refreshAuth } = useAuth();

    const email = searchParams.get('email');

    useEffect(() => {
        if (!email) {
            navigate('/login');
            return;
        }

        // Check OTP status
        checkOtpStatus();

        // Start countdown timer
        const timer = setInterval(() => {
            setTimeLeft(prev => {
                if (prev <= 1) {
                    clearInterval(timer);
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);

        return () => clearInterval(timer);
    }, [email, navigate]);

    const checkOtpStatus = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/otp/status?email=${email}`, {
                credentials: 'include'
            });
            const data = await response.json();
            
            if (!data.hasPendingLogin) {
                setError('No pending login found. Please try logging in again.');
                setTimeout(() => navigate('/login'), 3000);
            }
        } catch (error) {
            console.error('Error checking OTP status:', error);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!otpCode || otpCode.length !== 6) {
            setError('Please enter a valid 6-digit OTP code');
            return;
        }

        setLoading(true);
        setError('');
        setMessage('');

        try {
            const response = await fetch('http://localhost:8080/api/otp/verify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    email: email,
                    otpCode: otpCode
                })
            });

            const data = await response.json();

            if (data.success) {
                setMessage(data.message);
                // Ensure AuthContext reflects the new session before routing
                await refreshAuth();
                navigate(data.redirectUrl || '/dashboard');
            } else {
                setError(data.message);
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleResendOtp = async () => {
        setResendLoading(true);
        setError('');
        setMessage('');

        try {
            const response = await fetch('http://localhost:8080/api/otp/resend', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    email: email
                })
            });

            const data = await response.json();

            if (data.success) {
                setMessage(data.message);
                setTimeLeft(300); // Reset timer
                setOtpCode(''); // Clear current OTP
            } else {
                setError(data.message);
            }
        } catch (error) {
            setError('Failed to resend OTP. Please try again.');
        } finally {
            setResendLoading(false);
        }
    };

    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    const handleOtpChange = (e) => {
        const value = e.target.value.replace(/\D/g, ''); // Only allow digits
        if (value.length <= 6) {
            setOtpCode(value);
        }
    };

    return (
        <Container className="d-flex align-items-center justify-content-center min-vh-100">
            <Row className="w-100">
                <Col md={6} lg={4} className="mx-auto">
                    <Card className="shadow-lg border-0">
                        <Card.Header className="bg-primary text-white text-center py-4">
                            <h3 className="mb-0">🔐 Verify OTP</h3>
                        </Card.Header>
                        <Card.Body className="p-4">
                            <div className="text-center mb-4">
                                <p className="text-muted">
                                    We've sent a 6-digit verification code to:
                                </p>
                                <strong className="text-primary">{email}</strong>
                                <p className="text-muted mt-2 small">
                                    Please check your email and enter the code below
                                </p>
                            </div>

                            {error && (
                                <Alert variant="danger" className="mb-3">
                                    {error}
                                </Alert>
                            )}

                            {message && (
                                <Alert variant="success" className="mb-3">
                                    {message}
                                </Alert>
                            )}

                            <Form onSubmit={handleSubmit}>
                                <Form.Group className="mb-4">
                                    <Form.Label className="fw-bold">Enter OTP Code</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="000000"
                                        value={otpCode}
                                        onChange={handleOtpChange}
                                        className="text-center fs-4 py-3"
                                        style={{ letterSpacing: '0.5em' }}
                                        maxLength={6}
                                        required
                                        disabled={loading}
                                    />
                                    <Form.Text className="text-muted">
                                        Enter the 6-digit code sent to your email
                                    </Form.Text>
                                </Form.Group>

                                <div className="d-grid gap-2">
                                    <Button 
                                        variant="primary" 
                                        type="submit" 
                                        size="lg"
                                        disabled={loading || otpCode.length !== 6}
                                    >
                                        {loading ? (
                                            <>
                                                <Spinner size="sm" className="me-2" />
                                                Verifying...
                                            </>
                                        ) : (
                                            'Verify OTP'
                                        )}
                                    </Button>
                                </div>
                            </Form>

                            <div className="text-center mt-4">
                                <div className="mb-3">
                                    <span className="text-muted">Time remaining: </span>
                                    <span className={`fw-bold ${timeLeft < 60 ? 'text-danger' : 'text-primary'}`}>
                                        {formatTime(timeLeft)}
                                    </span>
                                </div>

                                <div>
                                    <span className="text-muted">Didn't receive the code? </span>
                                    <Button
                                        variant="link"
                                        className="p-0 text-decoration-none"
                                        onClick={handleResendOtp}
                                        disabled={resendLoading || timeLeft > 240} // Allow resend after 1 minute
                                    >
                                        {resendLoading ? (
                                            <>
                                                <Spinner size="sm" className="me-1" />
                                                Sending...
                                            </>
                                        ) : (
                                            'Resend OTP'
                                        )}
                                    </Button>
                                </div>

                                <div className="mt-3">
                                    <Button
                                        variant="outline-secondary"
                                        size="sm"
                                        onClick={() => navigate('/login')}
                                    >
                                        ← Back to Login
                                    </Button>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
}

export default OtpVerification;
