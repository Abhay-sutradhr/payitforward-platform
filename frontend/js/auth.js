// Authentication Logic for Pay It Forward Platform

document.addEventListener('DOMContentLoaded', function() {
    // Check if user is already logged in
    checkAuthStatus();
    
    // Initialize forms
    initializeAuthForms();
});

// Check authentication status
function checkAuthStatus() {
    const token = localStorage.getItem('payItForward_token');
    const currentPage = window.location.pathname;
    
    // If logged in and on auth pages, redirect to dashboard
    if (token && (currentPage.includes('login.html') || currentPage.includes('register.html'))) {
        // Could redirect to dashboard here in future
        console.log('User already logged in');
    }
}

// Initialize authentication forms
function initializeAuthForms() {
    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
        
        // Toggle password visibility
        const togglePassword = document.getElementById('togglePassword');
        if (togglePassword) {
            togglePassword.addEventListener('click', () => {
                const passwordInput = document.getElementById('password');
                const icon = togglePassword.querySelector('i');
                
                if (passwordInput.type === 'password') {
                    passwordInput.type = 'text';
                    icon.className = 'fas fa-eye-slash';
                } else {
                    passwordInput.type = 'password';
                    icon.className = 'fas fa-eye';
                }
            });
        }
    }
    
    // Registration form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegistration);
        
        // Toggle password visibility
        const togglePassword = document.getElementById('togglePassword');
        if (togglePassword) {
            togglePassword.addEventListener('click', () => {
                const passwordInput = document.getElementById('password');
                const icon = togglePassword.querySelector('i');
                
                if (passwordInput.type === 'password') {
                    passwordInput.type = 'text';
                    icon.className = 'fas fa-eye-slash';
                } else {
                    passwordInput.type = 'password';
                    icon.className = 'fas fa-eye';
                }
            });
        }
    }
}

// Handle login form submission
async function handleLogin(e) {
    e.preventDefault();
    
    const loginBtn = document.getElementById('loginBtn');
    const stopLoading = API.utils.showLoading(loginBtn, 'Signing in...');
    
    try {
        // Get form data
        const formData = {
            usernameOrEmail: document.getElementById('usernameOrEmail').value.trim(),
            password: document.getElementById('password').value
        };
        
        // Validate form
        if (!formData.usernameOrEmail || !formData.password) {
            throw new Error('Please fill in all required fields');
        }
        
        // Make login request
        const response = await API.auth.login(formData);
        
        // Store token and user info
        localStorage.setItem('payItForward_token', response.token);
        localStorage.setItem('payItForward_user', JSON.stringify({
            username: response.username,
            email: response.email
        }));
        
        // Show success message
        API.utils.showAlert(
            `Welcome back, ${response.username}! 🎉`, 
            'success'
        );
        
        // Redirect to dashboard after short delay
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);
        
    } catch (error) {
        console.error('Login failed:', error);
        API.utils.showAlert(
            `Login failed: ${error.message}`, 
            'danger'
        );
    } finally {
        stopLoading();
    }
}

// Handle registration form submission
async function handleRegistration(e) {
    e.preventDefault();
    
    const registerBtn = document.getElementById('registerBtn');
    const stopLoading = API.utils.showLoading(registerBtn, 'Creating account...');
    
    try {
        // Get form data
        const formData = {
            username: document.getElementById('username').value.trim(),
            email: document.getElementById('email').value.trim(),
            fullName: document.getElementById('fullName').value.trim(),
            password: document.getElementById('password').value,
            phone: document.getElementById('phone').value.trim(),
            address: document.getElementById('address').value.trim()
        };
        
        // Validate form
        if (!formData.username || !formData.email || !formData.fullName || !formData.password) {
            throw new Error('Please fill in all required fields');
        }
        
        // Validate email format
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(formData.email)) {
            throw new Error('Please enter a valid email address');
        }
        
        // Validate password strength
        if (formData.password.length < 6) {
            throw new Error('Password must be at least 6 characters long');
        }
        
        // Make registration request
        const response = await API.auth.register(formData);
        
        // Store token and user info
        localStorage.setItem('payItForward_token', response.token);
        localStorage.setItem('payItForward_user', JSON.stringify({
            username: response.username,
            email: response.email
        }));
        
        // Show success message with welcome bonus info
        API.utils.showAlert(
            `🎉 Welcome to Pay It Forward, ${response.username}!<br>
             <strong>You've received 10 free credits to get started!</strong>`, 
            'success'
        );
        
        // Redirect to dashboard after short delay
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 2000);
        
    } catch (error) {
        console.error('Registration failed:', error);
        let errorMessage = error.message;
        
        // Handle common registration errors
        if (errorMessage.includes('username') && errorMessage.includes('already exists')) {
            errorMessage = 'Username already taken. Please choose a different username.';
        } else if (errorMessage.includes('email') && errorMessage.includes('already exists')) {
            errorMessage = 'Email already registered. Please use a different email or sign in.';
        }
        
        API.utils.showAlert(
            `Registration failed: ${errorMessage}`, 
            'danger'
        );
    } finally {
        stopLoading();
    }
}

// Logout function (for future use)
function logout() {
    localStorage.removeItem('payItForward_token');
    localStorage.removeItem('payItForward_user');
    window.location.href = '../index.html';
}

// Get current user info from localStorage
function getCurrentUser() {
    const userStr = localStorage.getItem('payItForward_user');
    return userStr ? JSON.parse(userStr) : null;
}

// Check if user is authenticated
function isAuthenticated() {
    return !!localStorage.getItem('payItForward_token');
}

// Export functions for use in other scripts
window.AUTH = {
    logout,
    getCurrentUser,
    isAuthenticated,
    checkAuthStatus
};
