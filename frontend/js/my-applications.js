// My Applications functionality for Pay It Forward Platform

let currentApplications = [];

document.addEventListener('DOMContentLoaded', function() {
    // Check authentication
    if (!AUTH.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }
    
    // Initialize page
    initializePage();
    loadApplications();
});

function initializePage() {
    // Load user info in navbar
    const user = AUTH.getCurrentUser();
    if (user) {
        document.getElementById('username-display').textContent = user.username;
    }
}

async function loadApplications() {
    showLoading(true);
    
    try {
        const applications = await API.applications.getMyApplications();
        currentApplications = applications;
        displayApplications(applications);
        updateStatusSummary(applications);
    } catch (error) {
        console.error('Failed to load applications:', error);
        showError('Failed to load applications. Please try again.');
    } finally {
        showLoading(false);
    }
}

function displayApplications(applications) {
    const container = document.getElementById('applications-container');
    const emptyState = document.getElementById('empty-state');
    
    if (!applications || applications.length === 0) {
        container.innerHTML = '';
        emptyState.style.display = 'block';
        return;
    }
    
    emptyState.style.display = 'none';
    
    container.innerHTML = applications.map(app => createApplicationCard(app)).join('');
}

function createApplicationCard(application) {
    const statusColor = getStatusColor(application.status);
    const statusIcon = getStatusIcon(application.status);
    const job = application.job; // Assuming job details are included
    
    return `
        <div class="card mb-3 application-card" onclick="showApplicationDetails(${application.applicationId})">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <div class="d-flex align-items-start">
                            <div class="me-3">
                                <i class="${getCategoryIcon(job?.category)} fa-2x text-primary"></i>
                            </div>
                            <div class="flex-grow-1">
                                <h5 class="card-title mb-2">${job?.title || 'Job Title'}</h5>
                                <p class="text-muted mb-2">${job?.description?.substring(0, 100) || 'No description'}...</p>
                                <div class="job-details">
                                    <small class="text-muted me-3">
                                        <i class="fas fa-coins me-1"></i>${job?.creditCost || '0'} credits
                                    </small>
                                    <small class="text-muted me-3">
                                        <i class="fas fa-calendar me-1"></i>Applied: ${formatDate(application.appliedAt)}
                                    </small>
                                    ${job?.category ? 
                                        `<small class="text-muted">
                                            <i class="fas fa-tag me-1"></i>${job.category}
                                         </small>` : ''
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 text-end">
                        <span class="badge bg-${statusColor} fs-6 mb-2">
                            <i class="${statusIcon} me-1"></i>${formatStatus(application.status)}
                        </span>
                        <div>
                            ${application.reviewedAt ? 
                                `<small class="text-muted d-block">Reviewed: ${formatDate(application.reviewedAt)}</small>` : 
                                '<small class="text-muted d-block">Awaiting review</small>'
                            }
                        </div>
                        <button class="btn btn-outline-primary btn-sm mt-2">
                            <i class="fas fa-eye me-1"></i>View Details
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function updateStatusSummary(applications) {
    const summary = {
        pending: applications.filter(app => app.status === 'PENDING').length,
        accepted: applications.filter(app => app.status === 'ACCEPTED').length,
        rejected: applications.filter(app => app.status === 'REJECTED').length,
        total: applications.length
    };
    
    document.getElementById('pending-count').textContent = summary.pending;
    document.getElementById('accepted-count').textContent = summary.accepted;
    document.getElementById('rejected-count').textContent = summary.rejected;
    document.getElementById('total-count').textContent = summary.total;
}

function showApplicationDetails(applicationId) {
    const application = currentApplications.find(app => app.applicationId === applicationId);
    if (!application) return;
    
    const job = application.job;
    const modalContent = document.getElementById('modalContent');
    
    modalContent.innerHTML = `
        <div class="row">
            <div class="col-md-8">
                <h4>${job?.title || 'Job Title'}</h4>
                <p class="text-muted">${job?.description || 'No description available'}</p>
                
                <div class="job-info mt-3">
                    <div class="row">
                        <div class="col-sm-6">
                            <strong>Credit Reward:</strong> ${job?.creditCost || '0'} credits
                        </div>
                        <div class="col-sm-6">
                            <strong>Category:</strong> ${job?.category || 'N/A'}
                        </div>
                        <div class="col-sm-6">
                            <strong>Location:</strong> ${job?.location || 'Not specified'}
                        </div>
                        <div class="col-sm-6">
                            <strong>Urgency:</strong> ${job?.urgencyLevel || 'N/A'}
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="status-card p-3 bg-light rounded">
                    <h6>Application Status</h6>
                    <span class="badge bg-${getStatusColor(application.status)} fs-6 mb-2">
                        ${formatStatus(application.status)}
                    </span>
                    <div class="mt-3">
                        <small class="text-muted d-block">Applied: ${formatDate(application.appliedAt)}</small>
                        ${application.reviewedAt ? 
                            `<small class="text-muted d-block">Reviewed: ${formatDate(application.reviewedAt)}</small>` : ''
                        }
                    </div>
                </div>
            </div>
        </div>
        
        <hr>
        
        <div class="application-details">
            <h6>Your Application Message</h6>
            <div class="bg-light p-3 rounded">
                ${application.message}
            </div>
            
            ${application.qualifications ? `
                <h6 class="mt-3">Your Qualifications</h6>
                <div class="bg-light p-3 rounded">
                    ${application.qualifications}
                </div>
            ` : ''}
        </div>
    `;
    
    // Show withdraw button only for pending applications
    const withdrawBtn = document.getElementById('withdrawBtn');
    if (application.status === 'PENDING') {
        withdrawBtn.style.display = 'block';
        withdrawBtn.onclick = () => withdrawApplication(applicationId);
    } else {
        withdrawBtn.style.display = 'none';
    }
    
    const modal = new bootstrap.Modal(document.getElementById('applicationModal'));
    modal.show();
}

async function withdrawApplication(applicationId) {
    if (!confirm('Are you sure you want to withdraw this application? This action cannot be undone.')) {
        return;
    }
    
    try {
        await API.applications.withdrawApplication(applicationId);
        
        // Close modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('applicationModal'));
        modal.hide();
        
        // Show success message and reload
        showSuccess('Application withdrawn successfully');
        loadApplications();
        
    } catch (error) {
        console.error('Failed to withdraw application:', error);
        showError('Failed to withdraw application: ' + error.message);
    }
}

function applyFilters() {
    const status = document.getElementById('statusFilter').value;
    const category = document.getElementById('categoryFilter').value;
    
    let filteredApplications = currentApplications;
    
    if (status) {
        filteredApplications = filteredApplications.filter(app => app.status === status);
    }
    
    if (category) {
        filteredApplications = filteredApplications.filter(app => app.job?.category === category);
    }
    
    displayApplications(filteredApplications);
}

function getStatusColor(status) {
    const colors = {
        'PENDING': 'warning',
        'ACCEPTED': 'success',
        'REJECTED': 'danger',
        'WITHDRAWN': 'secondary'
    };
    return colors[status] || 'secondary';
}

function getStatusIcon(status) {
    const icons = {
        'PENDING': 'fas fa-clock',
        'ACCEPTED': 'fas fa-check-circle',
        'REJECTED': 'fas fa-times-circle',
        'WITHDRAWN': 'fas fa-ban'
    };
    return icons[status] || 'fas fa-question-circle';
}

function formatStatus(status) {
    const formatted = {
        'PENDING': 'Pending Review',
        'ACCEPTED': 'Accepted',
        'REJECTED': 'Rejected',
        'WITHDRAWN': 'Withdrawn'
    };
    return formatted[status] || status;
}

function getCategoryIcon(category) {
    const icons = {
        'TECHNICAL': 'fas fa-laptop-code',
        'PHYSICAL': 'fas fa-dumbbell',
        'ACADEMIC': 'fas fa-graduation-cap',
        'CREATIVE': 'fas fa-palette',
        'HOUSEHOLD': 'fas fa-home',
        'TRANSPORTATION': 'fas fa-car',
        'OTHER': 'fas fa-question-circle'
    };
    return icons[category] || 'fas fa-briefcase';
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        month: 'short', 
        day: 'numeric',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function showLoading(show) {
    document.getElementById('loading-container').style.display = show ? 'block' : 'none';
    document.getElementById('applications-container').style.display = show ? 'none' : 'block';
}

function showSuccess(message) {
    const toast = createToast(message, 'success');
    document.body.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
}

function showError(message) {
    const toast = createToast(message, 'danger');
    document.body.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
}

function createToast(message, type) {
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.style.position = 'fixed';
    toast.style.top = '20px';
    toast.style.right = '20px';
    toast.style.zIndex = '9999';
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    
    return toast;
}

// Logout function for navbar
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        AUTH.logout();
    }
}

// Global logout function
window.logout = logout;
