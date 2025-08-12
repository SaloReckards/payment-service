package com.iprody.payment.service.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    private final String operation;
    private final UUID entityid;

    public EntityNotFoundException(String message, String operation, UUID entityid) {
        super(message);
        this.operation = operation;
        this.entityid = entityid;
    }

    public UUID getEntityid() {
        return entityid;
    }

    public String getOperation() {
        return operation;
    }
}
