package com.iprody.payment.service.app.exception;

import java.time.Instant;
import java.util.UUID;

public class ErrorDto {
    private final int errorCode;
    private final UUID id;
    private final String errorMessage;
    private final Instant timestamp;
    private final String operation;

    public int getErrorCode() {
        return errorCode;
    }

    public ErrorDto(int errorCode, String error, String operation, UUID entityId) {
        this.errorCode = errorCode;
        this.errorMessage = error;
        this.operation = operation;
        this.id = entityId;
        this.timestamp = Instant.now();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getOperation() {
        return operation;
    }

    public UUID getId() {
        return id;
    }
}
