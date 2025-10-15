// Create Job functionality for Pay It Forward Platform

document.addEventListener('DOMContentLoaded', function() {
    // Check authentication
    if (!AUTH.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }
    
    // Initialize page
    initializePage();
});

function initializePage() {
    // Load user info
    const user = AUTH.getCurrentUser();
    if (user) {
        document.getElementById('username-display').textContent = user.username;
    }
    
    // Load credit balance (show default for now)
    document.getElementById('credit-balance').textContent = '10.0';
    
    // Initialize form
    const form = document.getElementById('createJobForm');
    form.addEventListener('submit', handleJobSubmission);
    
    // Add real-time validation
    addFormValidation();
}

function addFormValidation() {
    const creditInput = document.getElementById('creditCost');
    const titleInput = document.getElementById('jobTitle');
    const descriptionInput = document.getElementById('jobDescription');
    
    // Credit validation
    creditInput.addEventListener('input', function() {
        const value = parseFloat(this.value);
        const currentCredits = 10.0; // This should come from API later
        
        if (value > currentCredits) {
            this.setCustomValidity(`You only have ${currentCredits} credits available`);
            this.classList.add('is-invalid');
        } else if (value < 1) {
            this.setCustomValidity('Minimum reward is 1 credit');
            this.classList.add('is-invalid');
        } else {
            this.setCustomValidity('');
            this.classList.remove('is-invalid');
        }
    });
    
    // Character count for title
    titleInput.addEventListener('input', function() {
        updateCharacterCount(this, 200);
    });
    
    // Character count for description
    descriptionInput.addEventListener('input', function() {
        updateCharacterCount(this, 1000);
    });
}

function updateCharacterCount(input, maxLength) {
    const current = input.value.length;
    const remaining = maxLength - current;
    
    // Find or create character count element
    let countElement = input.parentNode.querySelector('.char-count');
    if (!countElement) {
        countElement = document.createElement('div');
        countElement.className = 'form-text char-count';
        input.parentNode.appendChild(countElement);
    }
    
    countElement.textContent = `${current}/${maxLength} characters`;
    countElement.className = `form-text char-count ${remaining < 50 ? 'text-warning' : ''}`;
}

async function handleJobSubmission(e) {
    e.preventDefault();
    
    const submitBtn = document.getElementById('submitBtn');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="loading"></span> Posting Job...';
    submitBtn.disabled = true;
    
    try {
        // Collect form data
        const jobData = {
            title: document.getElementById('jobTitle').value.trim(),
            description: document.getElementById('jobDescription').value.trim(),
            creditCost: parseFloat(document.getElementById('creditCost').value),
            category: document.getElementById('category').value,
            urgencyLevel: document.getElementById('urgencyLevel').value,
            estimatedHours: document.getElementById('estimatedHours').value ? 
                parseFloat(document.getElementById('estimatedHours').value) : null,
            isRemote: document.getElementById('isRemote').value === 'true',
            location: document.getElementById('location').value.trim() || null,
            requiredSkills: document.getElementById('requiredSkills').value.trim() || null,
            deadline: document.getElementById('deadline').value || null
        };
        
        // Validate required fields
        if (!jobData.title || !jobData.description || !jobData.creditCost || 
            !jobData.category || !jobData.urgencyLevel) {
            throw new Error('Please fill in all required fields');
        }
        
        // Submit job
        const response = await API.jobs.createJob(jobData);
        
        // Show success message
        showSuccess(`Job "${jobData.title}" posted successfully! 🎉`);
        
        // Redirect to dashboard after delay
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 2000);
        
    } catch (error) {
        console.error('Failed to create job:', error);
        showError('Failed to post job: ' + error.message);
    } finally {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
}

function previewJob() {
    const jobData = {
        title: document.getElementById('jobTitle').value.trim(),
        description: document.getElementById('jobDescription').value.trim(),
        creditCost: document.getElementById('creditCost').value,
        category: document.getElementById('category').value,
        urgencyLevel: document.getElementById('urgencyLevel').value,
        estimatedHours: document.getElementById('estimatedHours').value,
        isRemote: document.getElementById('isRemote').value === 'true',
        location: document.getElementById('location').value.trim(),
        requiredSkills: document.getElementById('requiredSkills').value.trim(),
        deadline: document.getElementById('deadline').value
    };
    
    const previewContainer = document.getElementById('jobPreview');
    const previewContent = document.getElementById('previewContent');
    
    if (!jobData.title || !jobData.description || !jobData.creditCost) {
        showError('Please fill in the basic information first');
        return;
    }
    
    const urgencyColor = getUrgencyColor(jobData.urgencyLevel);
    const categoryIcon = getCategoryIcon(jobData.category);
    
    previewContent.innerHTML = `
        <div class="d-flex justify-content-between align-items-start mb-3">
            <h5><i class="${categoryIcon} me-2 text-primary"></i>${jobData.title}</h5>
            <span class="badge bg-${urgencyColor}">${jobData.urgencyLevel}</span>
        </div>
        
        <p class="text-muted mb-3">${jobData.description}</p>
        
        <div class="row">
            <div class="col-md-6">
                <small class="text-muted d-block">
                    <i class="fas fa-coins me-1"></i><strong>Reward:</strong> ${jobData.creditCost} credits
                </small>
                <small class="text-muted d-block">
                    <i class="fas fa-tag me-1"></i><strong>Category:</strong> ${jobData.category}
                </small>
                ${jobData.estimatedHours ? 
                    `<small class="text-muted d-block">
                        <i class="fas fa-clock me-1"></i><strong>Estimated:</strong> ${jobData.estimatedHours} hours
                     </small>` : ''
                }
            </div>
            <div class="col-md-6">
                <small class="text-muted d-block">
                    <i class="fas fa-${jobData.isRemote ? 'laptop' : 'map-marker-alt'} me-1"></i>
                    <strong>Type:</strong> ${jobData.isRemote ? 'Remote' : 'In-person'}
                </small>
                ${jobData.location ? 
                    `<small class="text-muted d-block">
                        <i class="fas fa-map-marker-alt me-1"></i><strong>Location:</strong> ${jobData.location}
                     </small>` : ''
                }
                ${jobData.requiredSkills ? 
                    `<small class="text-muted d-block">
                        <i class="fas fa-tools me-1"></i><strong>Skills:</strong> ${jobData.requiredSkills}
                     </small>` : ''
                }
            </div>
        </div>
    `;
    
    previewContainer.style.display = 'block';
    previewContainer.scrollIntoView({ behavior: 'smooth' });
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

function showSuccess(message) {
    API.utils.showAlert(message, 'success', 'alert-container');
}

function showError(message) {
    API.utils.showAlert(message, 'danger', 'alert-container');
}

// Logout function for navbar
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        AUTH.logout();
    }
}

// Global logout function
window.logout = logout;
