// API Helper Functions for Pay It Forward Platform

// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to get JWT token from localStorage
function getAuthToken() {
    return localStorage.getItem('payItForward_token');
}

// Helper function to get auth headers
function getAuthHeaders() {
    const token = getAuthToken();
    return {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
    };
}

// Generic API request function
async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const defaultOptions = {
        method: 'GET',
        headers: getAuthHeaders(),
    };
    
    const requestOptions = { ...defaultOptions, ...options };
    
    try {
        const response = await fetch(url, requestOptions);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || `HTTP error! status: ${response.status}`);
        }
        
        return data;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

// Authentication API functions
const authAPI = {
    async login(credentials) {
        return apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials)
        });
    },
    
    async register(userData) {
        return apiRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    },
    
    async getCurrentUser() {
        return apiRequest('/auth/me');
    },
    
    async updateProfile(profileData) {
        return apiRequest('/auth/update', {
            method: 'PUT',
            body: JSON.stringify(profileData)
        });
    },
    
    async changePassword(passwordData) {
        return apiRequest('/auth/change-password', {
            method: 'PUT',
            body: JSON.stringify(passwordData)
        });
    }
};

// Jobs API functions
const jobsAPI = {
    async getAvailableJobs() {
        return apiRequest('/jobs/available');
    },
    
    async createJob(jobData) {
        return apiRequest('/jobs/create', {
            method: 'POST',
            body: JSON.stringify(jobData)
        });
    },
    
    async getMyJobs() {
        return apiRequest('/jobs/my-jobs');
    },
    
    async getJobById(jobId) {
        return apiRequest(`/jobs/${jobId}`);
    }
};

// Applications API functions
const applicationsAPI = {
    async applyToJob(jobId, applicationData) {
        return apiRequest(`/applications/apply/${jobId}`, {
            method: 'POST',
            body: JSON.stringify(applicationData)
        });
    },
    
    async getMyApplications() {
        return apiRequest('/applications/my-applications');
    },
    
    async getApplicationsForJob(jobId) {
        return apiRequest(`/applications/job/${jobId}`);
    },
    
    async acceptApplication(applicationId) {
        return apiRequest(`/applications/accept/${applicationId}`, {
            method: 'POST'
        });
    },
	
	async withdrawApplication(applicationId) {
	        return apiRequest(`/applications/withdraw/${applicationId}`, {
	            method: 'PUT'
	     });
	 }
};

// Credits API functions
const creditsAPI = {
    async getCreditHistory() {
        return apiRequest('/credits/history');
    },
    
    async getCreditSummary() {
        return apiRequest('/credits/summary');
    }
};

// Job completion API functions
const jobCompletionAPI = {
    async completeJob(jobId, completionData) {
        return apiRequest(`/jobs/complete/${jobId}`, {
            method: 'POST',
            body: JSON.stringify(completionData)
        });
    },
    
    async cancelJob(jobId, reason) {
        return apiRequest(`/jobs/cancel/${jobId}?reason=${encodeURIComponent(reason)}`, {
            method: 'POST'
        });
    }
};

// Utility functions
function showAlert(message, type = 'info', containerId = 'alert-container') {
    const alertContainer = document.getElementById(containerId);
    if (!alertContainer) return;
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    alertContainer.innerHTML = '';
    alertContainer.appendChild(alertDiv);
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => alertDiv.remove(), 150);
    }, 5000);
}

function showLoading(button, text = 'Loading...') {
    const originalText = button.innerHTML;
    button.innerHTML = `<span class="loading"></span> ${text}`;
    button.disabled = true;
    
    return () => {
        button.innerHTML = originalText;
        button.disabled = false;
    };
}

// Export for other scripts
window.API = {
    auth: authAPI,
    jobs: jobsAPI,
    applications: applicationsAPI,
    credits: creditsAPI,
    completion: jobCompletionAPI,
    utils: { showAlert, showLoading, getAuthToken }
};
