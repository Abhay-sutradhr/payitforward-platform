// Profile page logic

document.addEventListener('DOMContentLoaded', async () => {
  if (!AUTH.isAuthenticated()) {
    window.location.href = 'login.html';
    return;
  }

  loadUserInfo();
  await loadProfileStats();

  document.getElementById('profileForm').addEventListener('submit', updateProfile);
  document.getElementById('passwordForm').addEventListener('submit', changePassword);
});

async function loadUserInfo() {
  const user = AUTH.getCurrentUser();
  // Fill form fields
  document.getElementById('username').value = user.username;
  document.getElementById('email').value = user.email;

  // Fetch complete user details
  try {
    const data = await API.auth.getCurrentUser();
    document.getElementById('fullName').value = data.fullName;
    document.getElementById('phone').value = data.phone || '';
    document.getElementById('address').value = data.address || '';
  } catch (err) {
    console.error('Failed to load profile info', err);
  }
}

async function loadProfileStats() {
  try {
    const summary = await API.credits.getCreditSummary();
    document.getElementById('credit-balance').textContent = summary.totalEarned - summary.totalSpent;
    // For jobs completed & rating, we’ll use placeholder until endpoints exist
    document.getElementById('jobs-completed').textContent = summary.jobsCompleted || '0';
    document.getElementById('average-rating').textContent = summary.averageRating?.toFixed(1) || '0.0';
  } catch (err) {
    console.error('Failed loading stats', err);
  }
}

async function updateProfile(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type="submit"]');
  const restore = API.utils.showLoading(btn, 'Saving...');
  try {
    const payload = {
      fullName: document.getElementById('fullName').value.trim(),
      phone: document.getElementById('phone').value.trim(),
      address: document.getElementById('address').value.trim()
    };
    await API.auth.updateProfile(payload);
    API.utils.showAlert('Profile updated successfully!', 'success');
  } catch (err) {
    console.error(err);
    API.utils.showAlert('Failed to update profile: ' + err.message, 'danger');
  } finally {
    restore();
  }
}

async function changePassword(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type="submit"]');
  const restore = API.utils.showLoading(btn, 'Updating...');
  try {
    const payload = {
      currentPassword: document.getElementById('currentPassword').value,
      newPassword: document.getElementById('newPassword').value
    };
    await API.auth.changePassword(payload);
    API.utils.showAlert('Password changed successfully!', 'success');
    e.target.reset();
  } catch (err) {
    console.error(err);
    API.utils.showAlert('Failed to change password: ' + err.message, 'danger');
  } finally {
    restore();
  }
}

// Logout for profile page
window.logout = AUTH.logout;
