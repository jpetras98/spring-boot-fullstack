package com.janex.rest.handler;

import com.janex.exception.ApiException;
import com.janex.exception.DuplicateResourceException;
import com.janex.exception.RequestValidationException;
import com.janex.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ApiException> handleApiResourceNotFoundException(ResourceNotFoundException e) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;

        ApiException apiException = new ApiException(
                e.getMessage(),
                notFound,
                ZonedDateTime.now()
        );

        return new ResponseEntity<>(apiException, notFound);
    }


    @ExceptionHandler(value = {RequestValidationException.class})
    public ResponseEntity<ApiException> handleApiRequestValidationException(RequestValidationException e) {
        HttpStatus notFound = HttpStatus.BAD_REQUEST;

        ApiException apiException = new ApiException(
                e.getMessage(),
                notFound,
                ZonedDateTime.now()
        );

        return new ResponseEntity<>(apiException, notFound);
    }


    @ExceptionHandler(value = {DuplicateResourceException.class})
    public ResponseEntity<ApiException> handleApiRequestValidationException(DuplicateResourceException e) {
        HttpStatus notFound = HttpStatus.CONFLICT;

        ApiException apiException = new ApiException(
                e.getMessage(),
                notFound,
                ZonedDateTime.now()
        );

        return new ResponseEntity<>(apiException, notFound);
    }
}
