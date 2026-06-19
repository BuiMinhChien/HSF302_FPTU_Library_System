class FormValidator {

    static required(inputId, errorId, message) {
        const input = document.getElementById(inputId);
        const error = document.getElementById(errorId);
        if (!input.value.trim()) {
            error.textContent = message;
            error.style.display = "block";
            input.classList.add("is-invalid");
            return false;
        }
        error.textContent = "";
        error.style.display = "none";
        input.classList.remove("is-invalid");
        return true;
    }

    static email(inputId, errorId) {
        const input = document.getElementById(inputId);
        const error = document.getElementById(errorId);
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!regex.test(input.value.trim())) {
            error.textContent = "Email không đúng định dạng";
            error.style.display = "block";
            input.classList.add("is-invalid");
            return false;
        }
        error.textContent = "";
        error.style.display = "none";
        input.classList.remove("is-invalid");
        return true;
    }

    static minLength(inputId, errorId, min, message) {
        const input = document.getElementById(inputId);
        const error = document.getElementById(errorId);
        if (input.value.trim().length < min) {
            error.textContent = message;
            error.style.display = "block";
            input.classList.add("is-invalid");
            return false;
        }
        error.textContent = "";
        error.style.display = "none";
        input.classList.remove("is-invalid");
        return true;
    }
}