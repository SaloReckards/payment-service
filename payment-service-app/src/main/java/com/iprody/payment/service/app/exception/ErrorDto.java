package com.iprody.payment.service.app.exception;

import java.time.Instant;
import java.util.UUID;

public record ErrorDto(int errorCode, UUID id, String errorMessage, String operation, Instant timestamp) {


    public ErrorDto(int errorCode, UUID id, String errorMessage, String operation) {
        this(errorCode, id, errorMessage, operation, Instant.now());
    }
}
