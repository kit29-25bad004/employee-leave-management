/**
 * Employee Leave Management System (ELMS) - Frontend Script
 */

document.addEventListener("DOMContentLoaded", function () {

    // 1. Password Visibility Toggle
    const togglePasswordBtn = document.getElementById("togglePasswordBtn");
    const passwordInput = document.getElementById("passwordInput");

    if (togglePasswordBtn && passwordInput) {
        togglePasswordBtn.addEventListener("click", function () {
            const isPassword = passwordInput.getAttribute("type") === "password";
            passwordInput.setAttribute("type", isPassword ? "text" : "password");
            togglePasswordBtn.textContent = isPassword ? "Hide" : "Show";
        });
    }

    // 2. Demo Login Quick-Fill Helper
    window.fillCredentials = function (username, password) {
        const usernameInput = document.getElementById("usernameInput");
        const passInput = document.getElementById("passwordInput");
        if (usernameInput && passInput) {
            usernameInput.value = username;
            passInput.value = password;
        }
    };

    // 3. Dynamic Leave Days Calculation
    const fromDateInput = document.getElementById("fromDate");
    const toDateInput = document.getElementById("toDate");
    const daysInput = document.getElementById("calculatedDays");
    const dateHelpText = document.getElementById("dateHelpText");

    function calculateLeaveDays() {
        if (!fromDateInput || !toDateInput || !daysInput) return;

        const fromVal = fromDateInput.value;
        const toVal = toDateInput.value;

        if (fromVal && toVal) {
            const start = new Date(fromVal);
            const end = new Date(toVal);

            if (start > end) {
                if (dateHelpText) {
                    dateHelpText.textContent = "Error: From Date cannot be after To Date.";
                    dateHelpText.style.color = "#dc2626";
                }
                daysInput.value = "";
                return;
            }

            // Calculate difference in days (inclusive)
            const diffTime = Math.abs(end - start);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

            daysInput.value = diffDays;
            if (dateHelpText) {
                dateHelpText.textContent = diffDays + " day(s) requested (inclusive).";
                dateHelpText.style.color = "#15803d";
            }
        }
    }

    if (fromDateInput && toDateInput) {
        fromDateInput.addEventListener("change", calculateLeaveDays);
        toDateInput.addEventListener("change", calculateLeaveDays);
    }

    // 4. Mobile Sidebar Toggle
    const mobileToggle = document.getElementById("mobileMenuToggle");
    const sidebar = document.querySelector(".sidebar");
    if (mobileToggle && sidebar) {
        mobileToggle.addEventListener("click", function () {
            sidebar.classList.toggle("open");
        });
    }

    // 5. Submit Button Loading States
    const activeForms = document.querySelectorAll("form.needs-loading-state");
    activeForms.forEach(function (form) {
        form.addEventListener("submit", function (e) {
            const submitBtn = form.querySelector("button[type='submit']");
            if (submitBtn) {
                submitBtn.disabled = true;
                const originalText = submitBtn.textContent;
                submitBtn.innerHTML = "Processing...";
                // Re-enable if navigation is cancelled
                setTimeout(function () {
                    submitBtn.disabled = false;
                    submitBtn.textContent = originalText;
                }, 8000);
            }
        });
    });

    // 6. Delete Confirmation Modal
    window.confirmEmployeeDelete = function (employeeId, employeeName) {
        const modal = document.getElementById("deleteConfirmModal");
        const empNameTarget = document.getElementById("deleteEmployeeName");
        const empIdInput = document.getElementById("deleteEmployeeIdInput");

        if (modal && empNameTarget && empIdInput) {
            empNameTarget.textContent = employeeName + " (" + employeeId + ")";
            empIdInput.value = employeeId;
            modal.style.display = "flex";
        }
    };

    window.closeDeleteModal = function () {
        const modal = document.getElementById("deleteConfirmModal");
        if (modal) {
            modal.style.display = "none";
        }
    };
});
