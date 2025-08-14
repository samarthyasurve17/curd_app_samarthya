import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import axios from 'axios';

function Dashboard() {
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalUsers: 0,
    recentProducts: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const { currentUser } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const productsResponse = await axios.get('/api/products');

      if (productsResponse.data.success) {
        const products = productsResponse.data.products || [];
        setStats(prev => ({
          ...prev,
          totalProducts: products.length,
          recentProducts: products.slice(0, 5)
        }));
      }
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-spinner">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Dashboard</h1>
        <div>
          <Button 
            variant="primary" 
            className="me-2"
            onClick={() => navigate('/products/new')}
          >
            Add Product
          </Button>
          <Button 
            variant="outline-primary"
            onClick={() => navigate('/products')}
          >
            View All Products
          </Button>
        </div>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      <Row className="mb-4">
        <Col md={4}>
          <Card className="text-center h-100">
            <Card.Body>
              <Card.Title>Welcome Back!</Card.Title>
              <Card.Text>
                Hello, <strong>{currentUser?.fullName || currentUser?.username}</strong>
              </Card.Text>
              <Card.Text className="text-muted">
                Role: {currentUser?.role || 'User'}
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center h-100">
            <Card.Body>
              <Card.Title>Total Products</Card.Title>
              <Card.Text className="display-4 text-primary">
                {stats.totalProducts}
              </Card.Text>
              <Card.Text className="text-muted">
                Products in inventory
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center h-100">
            <Card.Body>
              <Card.Title>Quick Actions</Card.Title>
              <div className="d-grid gap-2">
                <Button 
                  variant="outline-primary" 
                  size="sm"
                  onClick={() => navigate('/products/new')}
                >
                  Add New Product
                </Button>
                <Button 
                  variant="outline-secondary" 
                  size="sm"
                  onClick={() => navigate('/products')}
                >
                  Manage Products
                </Button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col md={8}>
          <Card>
            <Card.Header>
              Recent Products
            </Card.Header>
            <Card.Body>
              {stats.recentProducts.length > 0 ? (
                <div className="table-responsive">
                  <table className="table table-hover">
                    <thead>
                      <tr>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Quantity</th>
                      </tr>
                    </thead>
                    <tbody>
                      {stats.recentProducts.map(product => (
                        <tr 
                          key={product.id}
                          style={{ cursor: 'pointer' }}
                          onClick={() => navigate(`/products/edit/${product.id}`)}
                        >
                          <td>{product.name}</td>
                          <td>
                            <span className="badge bg-secondary">
                              {product.category || 'Uncategorized'}
                            </span>
                          </td>
                          <td>${product.price}</td>
                          <td>
                            <span className={`badge ${product.quantity > 0 ? 'bg-success' : 'bg-danger'}`}>
                              {product.quantity}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="empty-state">
                  <i className="bi bi-box"></i>
                  <p>No products found</p>
                  <Button 
                    variant="primary"
                    onClick={() => navigate('/products/new')}
                  >
                    Add Your First Product
                  </Button>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card>
            <Card.Header>
              System Information
            </Card.Header>
            <Card.Body>
              <div className="mb-3">
                <strong>Application:</strong> CRUD Application
              </div>
              <div className="mb-3">
                <strong>Backend:</strong> Spring Boot + JPA
              </div>
              <div className="mb-3">
                <strong>Frontend:</strong> React.js + Bootstrap
              </div>
              <div className="mb-3">
                <strong>Database:</strong> MySQL
              </div>
              <div className="mb-3">
                <strong>Last Updated:</strong> {new Date().toLocaleDateString()}
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default Dashboard;
