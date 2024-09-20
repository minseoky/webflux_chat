package com.example.demo.exception;

import com.example.demo.dto.response.ResponseDto;
import com.example.demo.exception.exceptions.LoginFailedException;
import com.example.demo.exception.exceptions.UserNotFoundException;
import com.example.demo.exception.exceptions.UsernameDuplicatedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<? extends ResponseDto<?>> handleResourceNotFoundException(Exception ex) {
        String message = ex.getMessage();
        if(ex instanceof NoResourceFoundException) {
            message = "No resource found";
        }
        return ResponseDto.resourceNotFound(message);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<? extends ResponseDto<?>> authenticationFailedException(Exception ex) {
        return ResponseDto.authenticationFailed(ex.getMessage());
    }

    @ExceptionHandler(UsernameDuplicatedException.class)
    public ResponseEntity<? extends ResponseDto<?>> handleResourceDuplicatedException(Exception ex) {
        return ResponseDto.resourceDuplicated(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<? extends ResponseDto<?>> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return ResponseDto.internalServerError(ex.getMessage());
    }

}
