package com.zybooks.inventoryapp.model;

// A representation for validation.
public class ValidationResult {
    private boolean hasError;
    private String errorMessage;

    public ValidationResult(boolean hasError, String errorMessage) {
        this.hasError = hasError;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errMessage) {
        this.errorMessage = errMessage;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
