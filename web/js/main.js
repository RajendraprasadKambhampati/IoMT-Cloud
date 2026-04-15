/**
 * IoMT Secure Cloud Storage - Main JavaScript
 * Handles UI interactions, sidebar toggle, form validation, and dynamic elements
 */

// ===== Sidebar Toggle (Mobile) =====
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    if (sidebar) {
        sidebar.classList.toggle('open');
    }
    if (overlay) {
        overlay.classList.toggle('active');
    }
}

// Close sidebar when clicking overlay
document.addEventListener('DOMContentLoaded', function() {
    const overlay = document.querySelector('.sidebar-overlay');
    if (overlay) {
        overlay.addEventListener('click', function() {
            toggleSidebar();
        });
    }

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            alert.style.transition = 'all 0.3s ease';
            setTimeout(function() {
                alert.remove();
            }, 300);
        }, 5000);
    });

    // Initialize file upload drag-and-drop
    initFileUpload();

    // Initialize policy builder
    initPolicyBuilder();

    // Add animation to stat cards
    animateStatCards();
});

// ===== File Upload Drag & Drop =====
function initFileUpload() {
    const uploadArea = document.querySelector('.upload-area');
    const fileInput = document.getElementById('fileInput');

    if (!uploadArea || !fileInput) return;

    uploadArea.addEventListener('click', function() {
        fileInput.click();
    });

    uploadArea.addEventListener('dragover', function(e) {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', function() {
        uploadArea.classList.remove('dragover');
    });

    uploadArea.addEventListener('drop', function(e) {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        if (e.dataTransfer.files.length > 0) {
            fileInput.files = e.dataTransfer.files;
            updateFileName(fileInput);
        }
    });

    fileInput.addEventListener('change', function() {
        updateFileName(this);
    });
}

function updateFileName(input) {
    const uploadArea = document.querySelector('.upload-area');
    const fileName = input.files[0] ? input.files[0].name : '';
    let nameEl = uploadArea.querySelector('.file-name');

    if (fileName) {
        if (!nameEl) {
            nameEl = document.createElement('p');
            nameEl.className = 'file-name';
            uploadArea.appendChild(nameEl);
        }
        const size = (input.files[0].size / 1024 / 1024).toFixed(2);
        nameEl.textContent = '📄 ' + fileName + ' (' + size + ' MB)';
    }
}

// ===== Policy Builder =====
function initPolicyBuilder() {
    const policyRole = document.getElementById('policyRole');
    const policyDept = document.getElementById('policyDept');
    const policyLevel = document.getElementById('policyLevel');
    const policyPreview = document.getElementById('policyPreview');

    if (!policyRole || !policyPreview) return;

    function updatePolicy() {
        let parts = [];
        if (policyRole && policyRole.value && policyRole.value !== 'any') {
            parts.push('role=' + policyRole.value);
        }
        if (policyDept && policyDept.value && policyDept.value !== 'any') {
            parts.push('dept=' + policyDept.value);
        }
        if (policyLevel && policyLevel.value && parseInt(policyLevel.value) > 0) {
            parts.push('level>=' + policyLevel.value);
        }
        policyPreview.textContent = parts.length > 0 ? parts.join(' AND ') : 'No restrictions (accessible by all)';
    }

    if (policyRole) policyRole.addEventListener('change', updatePolicy);
    if (policyDept) policyDept.addEventListener('change', updatePolicy);
    if (policyLevel) policyLevel.addEventListener('change', updatePolicy);
    updatePolicy();
}

// ===== Stat Card Animations =====
function animateStatCards() {
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach(function(card, index) {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        setTimeout(function() {
            card.style.transition = 'all 0.4s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
}

// ===== Form Validation =====
function validateLoginForm() {
    const email = document.getElementById('email');
    const password = document.getElementById('password');

    if (!email.value.trim()) {
        showFormError(email, 'Email is required');
        return false;
    }
    if (!password.value.trim()) {
        showFormError(password, 'Password is required');
        return false;
    }
    return true;
}

function validateRegisterForm() {
    const name = document.getElementById('name');
    const email = document.getElementById('email');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    if (!name.value.trim()) {
        showFormError(name, 'Name is required');
        return false;
    }
    if (!email.value.trim()) {
        showFormError(email, 'Email is required');
        return false;
    }
    if (password.value.length < 6) {
        showFormError(password, 'Password must be at least 6 characters');
        return false;
    }
    if (password.value !== confirmPassword.value) {
        showFormError(confirmPassword, 'Passwords do not match');
        return false;
    }
    return true;
}

function showFormError(input, message) {
    input.style.borderColor = '#f43f5e';
    input.focus();

    // Remove existing error
    const existing = input.parentNode.querySelector('.field-error');
    if (existing) existing.remove();

    const error = document.createElement('span');
    error.className = 'field-error';
    error.style.color = '#f43f5e';
    error.style.fontSize = '0.8rem';
    error.style.marginTop = '4px';
    error.style.display = 'block';
    error.textContent = message;
    input.parentNode.appendChild(error);

    input.addEventListener('input', function() {
        input.style.borderColor = '';
        const err = input.parentNode.querySelector('.field-error');
        if (err) err.remove();
    }, { once: true });
}

// ===== Confirm Actions =====
function confirmAction(message) {
    return confirm(message);
}

// ===== Copy to Clipboard =====
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(function() {
        // Show brief tooltip
        const tooltip = document.createElement('div');
        tooltip.textContent = 'Copied!';
        tooltip.style.cssText = 'position:fixed;top:20px;right:20px;background:#10b981;color:white;padding:8px 16px;border-radius:8px;font-size:0.85rem;z-index:9999;animation:fadeIn 0.2s ease;';
        document.body.appendChild(tooltip);
        setTimeout(function() { tooltip.remove(); }, 2000);
    });
}
