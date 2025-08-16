package com.iprody.payment.service.app.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFound(EntityNotFoundException ex) {
        return new ErrorDto(HttpStatus.NOT_FOUND.value(), ex.getEntityid(), ex.getOperation(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleOther(Exception ex) {
        return new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),null, ex.getMessage(), null);
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleTypeMismatch(TypeMismatchException ex) {
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(),  null, ex.getMessage(), null);
    }
}
