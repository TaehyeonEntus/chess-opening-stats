package com.chessopeningstats.backend.exception;

public class DashboardNotFoundException extends BusinessException {
    public DashboardNotFoundException() {
        super("Dashboard not found");
    }

    public DashboardNotFoundException(String message) {
        super(message);
    }

    public DashboardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DashboardNotFoundException(Throwable cause) {
        super(cause);
    }
}
