// Browse Jobs functionality for Pay It Forward Platform

let currentJobs = [];
let selectedJobId = null;

document.addEventListener('DOMContentLoaded', function() {
    // Check authentication
    if (!AUTH.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }
    
    // Initialize page
    initializePage();
    loadJobs();
});

function initializePage() {
    // Load user info in navbar
    const user = AUTH.getCurrentUser();
    if (user) {
        document.getElementById('username-display').textContent = user.username;
    }
    
    // Initialize application modal
    const applicationModal = document.getElementById('applicationModal');
    const submitBtn = document.getElementById('submitApplication');
    
    submitBtn.addEventListener('click', submitApplication);
}

async function loadJobs() {
    showLoading(true);
    
    try {
        const jobs = await API.jobs.getAvailableJobs();
        currentJobs = jobs;
        displayJobs(jobs);
    } catch (error) {
        console.error('Failed to load jobs:', error);
        showError('Failed to load jobs. Please try again.');
    } finally {
        showLoading(false);
    }
}

function displayJobs(jobs) {
    const container = document.getElementById('jobs-container');
    const emptyState = document.getElementById('empty-state');
    
    if (!jobs || jobs.length === 0) {
        container.innerHTML = '';
        emptyState.style.display = 'block';
        return;
    }
    
    emptyState.style.display = 'none';
    
    container.innerHTML = jobs.map(job => createJobCard(job)).join('');
}

function createJobCard(job) {
    const urgencyColor = getUrgencyColor(job.urgencyLevel);
    const categoryIcon = getCategoryIcon(job.category);
    
    return `
        <div class="col-lg-6 col-xl-4 mb-4">
            <div class="card job-card h-100">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="badge bg-${urgencyColor}">${job.urgencyLevel}</span>
                    <span class="text-muted">
                        <i class="fas fa-coins me-1"></i>${job.creditCost} credits
                    </span>
                </div>
                <div class="card-body">
                    <h5 class="card-title">
                        <i class="${categoryIcon} me-2 text-primary"></i>
                        ${job.title}
                    </h5>
                    <p class="card-text text-muted mb-2">
                        ${job.description.length > 120 ? job.description.substring(0, 120) + '...' : job.description}
                    </p>
                    
                    <div class="job-details mb-3">
                        <small class="text-muted d-block">
                            <i class="fas fa-tag me-1"></i>Category: ${job.category}
                        </small>
                        ${job.estimatedHours ? 
                            `<small class="text-muted d-block">
                                <i class="fas fa-clock me-1"></i>Estimated: ${job.estimatedHours} hours
                             </small>` : ''
                        }
                        ${job.location ? 
                            `<small class="text-muted d-block">
                                <i class="fas fa-map-marker-alt me-1"></i>${job.location}
                             </small>` : ''
                        }
                        ${job.requiredSkills ? 
                            `<small class="text-muted d-block">
                                <i class="fas fa-tools me-1"></i>Skills: ${job.requiredSkills}
                             </small>` : ''
                        }
                        <small class="text-muted d-block">
                            <i class="fas fa-calendar me-1"></i>Posted: ${formatDate(job.createdAt)}
                        </small>
                    </div>
                </div>
                <div class="card-footer bg-white border-0">
                    <button class="btn btn-primary w-100" onclick="openApplicationModal(${job.jobId})">
                        <i class="fas fa-hand-paper me-1"></i>Apply to Help
                    </button>
                </div>
            </div>
        </div>
    `;
}

function getUrgencyColor(urgency) {
    const colors = {
        'URGENT': 'danger',
        'HIGH': 'warning',
        'MEDIUM': 'info',
        'LOW': 'secondary'
    };
    return colors[urgency] || 'secondary';
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
        hour: '2-digit',
        minute: '2-digit'
    });
}

function openApplicationModal(jobId) {
    selectedJobId = jobId;
    const modal = new bootstrap.Modal(document.getElementById('applicationModal'));
    
    // Reset form
    document.getElementById('applicationForm').reset();
    
    modal.show();
}

async function submitApplication() {
    if (!selectedJobId) return;
    
    const message = document.getElementById('applicationMessage').value.trim();
    const qualifications = document.getElementById('applicationQualifications').value.trim();
    
    if (!message) {
        alert('Please provide a message explaining why you want to help.');
        return;
    }
    
    const submitBtn = document.getElementById('submitApplication');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="loading"></span> Submitting...';
    submitBtn.disabled = true;
    
    try {
        await API.applications.applyToJob(selectedJobId, {
            message: message,
            qualifications: qualifications
        });
        
        // Close modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('applicationModal'));
        modal.hide();
        
        // Show success message
        showSuccess('Application submitted successfully! The job poster will review your application.');
        
        // Reload jobs to update the list
        loadJobs();
        
    } catch (error) {
        console.error('Failed to submit application:', error);
        showError('Failed to submit application: ' + error.message);
    } finally {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
}

function applyFilters() {
    const category = document.getElementById('categoryFilter').value;
    const urgency = document.getElementById('urgencyFilter').value;
    const search = document.getElementById('searchInput').value.toLowerCase();
    
    let filteredJobs = currentJobs;
    
    if (category) {
        filteredJobs = filteredJobs.filter(job => job.category === category);
    }
    
    if (urgency) {
        filteredJobs = filteredJobs.filter(job => job.urgencyLevel === urgency);
    }
    
    if (search) {
        filteredJobs = filteredJobs.filter(job => 
            job.title.toLowerCase().includes(search) ||
            job.description.toLowerCase().includes(search) ||
            (job.requiredSkills && job.requiredSkills.toLowerCase().includes(search))
        );
    }
    
    displayJobs(filteredJobs);
}

function showLoading(show) {
    document.getElementById('loading-container').style.display = show ? 'block' : 'none';
    document.getElementById('jobs-container').style.display = show ? 'none' : 'block';
}

function showSuccess(message) {
    // Create and show success toast
    const toast = createToast(message, 'success');
    document.body.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
}

function showError(message) {
    // Create and show error toast
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
