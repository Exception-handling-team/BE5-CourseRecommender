package com.jmy.courseRecommender.global.exception;

import com.jmy.courseRecommender.global.dto.RsData;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패 시 발생하는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + " : " + fe.getCode() + " : " + fe.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("\n"));

        RsData<Void> rsData = new RsData<>("400-1", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(rsData);
    }

    // ServiceException 처리
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RsData<Void>> handleServiceException(ServiceException ex) {

        RsData<Void> rsData = new RsData<>(ex.getCode(), ex.getMsg());
        return ResponseEntity
                .status(HttpStatus.valueOf(ex.getStatusCode()))
                .body(rsData);
    }

    // 그 외 모든 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        RsData<Void> rsData = new RsData<>("500-1", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(rsData);
    }
}