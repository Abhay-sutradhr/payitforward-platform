// Dashboard functionality for Pay It Forward Platform

document.addEventListener('DOMContentLoaded', function() {
    // Check if user is authenticated
    if (!AUTH.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }
    
    // Initialize dashboard
    initializeDashboard();
});

async function initializeDashboard() {
    try {
        // Load user info
        loadUserInfo();
        
        // Load dashboard data (we'll implement this progressively)
        await loadDashboardStats();
        
    } catch (error) {
        console.error('Dashboard initialization failed:', error);
    }
}

function loadUserInfo() {
    const user = AUTH.getCurrentUser();
    if (user) {
        // Update username displays
        document.getElementById('username-display').textContent = user.username;
        document.getElementById('welcome-username').textContent = user.username;
    }
}

async function loadDashboardStats() {
    try {
        // For now, we'll show default values
        // Later we can load real data from the API
        
        // You can uncomment these when we create the backend endpoints
        // const creditSummary = await API.credits.getCreditSummary();
        // const myJobs = await API.jobs.getMyJobs();
        // const myApplications = await API.applications.getMyApplications();
        
        // Update stats with default values for now
        updateStatsDisplay({
            credits: '10.0',
            jobsCompleted: '0', 
            jobsPosted: '0',
            averageRating: '5.0'
        });
        
    } catch (error) {
        console.error('Failed to load dashboard stats:', error);
    }
}

function updateStatsDisplay(stats) {
    document.getElementById('credit-balance').textContent = stats.credits;
    document.getElementById('jobs-completed').textContent = stats.jobsCompleted;
    document.getElementById('jobs-posted').textContent = stats.jobsPosted;
    document.getElementById('average-rating').textContent = stats.averageRating;
}

// Logout function
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        AUTH.logout();
    }
}

// Global logout function for navbar
window.logout = logout;
