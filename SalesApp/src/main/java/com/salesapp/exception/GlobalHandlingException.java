package com.salesapp.exception;

import com.salesapp.dto.response.ResponseObject;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalHandlingException {
    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ResponseObject> HandlingRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(1000)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ResponseObject> HandlingAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(errorCode.getCode());
        responseObject.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(responseObject);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ResponseObject> HandlingAccessDeniedException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZE;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
            ResponseObject.builder()
                    .status(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ResponseObject> HandlingValidation(MethodArgumentNotValidException e) {
        String messageKey = e.getFieldError().getDefaultMessage();
        ErrorCode errorCode = null;
        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(messageKey);

            var constraintViolations = e.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);

            attributes = constraintViolations.getConstraintDescriptor().getAttributes();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(errorCode.getCode())
                    .message(messageKey) // Hiển thị luôn message gốc
                    .build());
        }
        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(errorCode.getCode())
                .message(Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage())
                .build());
    }
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseObject> handleUnexpectedException(Exception exception) {
        exception.printStackTrace(); // nên dùng log.error() nếu đã cấu hình logging
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(ErrorCode.UNAUTHENTICATED.getCode());
        responseObject.setMessage(ErrorCode.UNAUTHENTICATED.getMessage());

        return ResponseEntity.badRequest().body(responseObject);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE +"}", minValue);
    }
}
