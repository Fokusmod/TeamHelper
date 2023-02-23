package ru.geekbrains.WowVendorTeamHelper.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<AppError> catchResourceNotFoundException(ResourceNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchSlackException(SlackException e) {
        log.error(e.getMessage(),e);
        return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> containsLiteralsException(RequestContainsLiteralsException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> containsLiteralsException(FailedEventListCreationException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}