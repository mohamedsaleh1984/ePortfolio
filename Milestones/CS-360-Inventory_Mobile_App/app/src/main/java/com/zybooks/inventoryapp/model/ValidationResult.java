package com.zybooks.inventoryapp.model;

//TODO: Add Comments.
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

    public boolean hasError() {
        return this.hasError;
    }

    public void setErrorMessage(String errMessage) {
        this.errorMessage = errMessage;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
