// Main JavaScript for Pay It Forward Platform

document.addEventListener('DOMContentLoaded', function() {
    console.log('Pay It Forward Platform Loaded');
    
    // Smooth scrolling for navigation links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // Add animation to stats when they come into view
    const stats = document.querySelectorAll('.display-5');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animation = 'countUp 2s ease-out';
            }
        });
    });
    
    stats.forEach(stat => observer.observe(stat));
});

// Add count-up animation
const style = document.createElement('style');
style.textContent = `
    @keyframes countUp {
        from { opacity: 0; transform: translateY(20px); }
        to { opacity: 1; transform: translateY(0); }
    }
`;
document.head.appendChild(style);
