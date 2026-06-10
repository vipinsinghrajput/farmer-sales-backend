package com.farmerapp.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.farmerapp.exception.AccountDeactivatedException;
import com.farmerapp.exception.DateFormateException;
import com.farmerapp.exception.DublicateEmailException;
import com.farmerapp.exception.EmptyOrNullFoundlException;
import com.farmerapp.exception.ExpiredTokenException;
import com.farmerapp.exception.ImageFileRequiredException;
import com.farmerapp.exception.InvalidPasswordException;
import com.farmerapp.exception.InvalidStatusException;
import com.farmerapp.exception.NotificationNotFoundException;
import com.farmerapp.exception.OrderNotFoundException;
import com.farmerapp.exception.OtpExpiredException;
import com.farmerapp.exception.OtpNotVerifiedException;
import com.farmerapp.exception.ProductNotFoundException;
import com.farmerapp.exception.UnauthorizedAccessException;
import com.farmerapp.exception.UnauthorizedStatusUpdateException;
import com.farmerapp.exception.UserAlreadyExistsException;
import com.farmerapp.exception.UserNotFoundException;
import com.farmerapp.response.ApiResponse;
import com.farmerapp.response.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice

public class CustomHandler{

	
	
	 @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	    public ResponseEntity<ApiResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
	        return ResponseEntity.badRequest().body(ApiResponse.builder()
	                .message("Invalid HTTP method used. Use the correct method like POST or GET.")
	                .build());
	    }
	 
	 @ExceptionHandler(ImageFileRequiredException.class)
	    public ResponseEntity<String> handleImageFileRequiredException(ImageFileRequiredException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(HttpMessageNotReadableException.class)
	    public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
	        return ResponseEntity.badRequest().body(
	            ApiResponse.builder()
	                .message("Invalid or missing request body. Please send valid JSON data.")
	                .build()
	        );
	    }
	
	
	  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
	    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(ApiResponse.builder()
	                        .message("Invalid  status: " + ex.getValue())
	                        .build());
	    }
	
	
	  @ExceptionHandler(NoResourceFoundException.class)
	    public ResponseEntity<ApiResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(ApiResponse.builder().message("Invalid API endpoint or resource not found").build());
	    }
	
	 @ExceptionHandler(ProductNotFoundException.class)
	    public ResponseEntity<ApiResponse> handleProductNotFoundException(ProductNotFoundException ex) {
	        ApiResponse response = ApiResponse.builder()
	                .message(ex.getMessage())
	                .response(null)
	                .build();

	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    }
	 @ExceptionHandler(OrderNotFoundException.class)
	    public ResponseEntity<ApiResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
	        ApiResponse response = ApiResponse.builder()
	                .message(ex.getMessage())
	                .response(null)
	                .build();

	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    }
	
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> name(UserAlreadyExistsException ex) {
		return new ResponseEntity<ErrorResponse> (new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
				

	}
	
	
	 @ExceptionHandler(ExpiredTokenException.class)
	    public ResponseEntity<ApiResponse> name(ExpiredTokenException ex) {
	        ApiResponse response = ApiResponse.builder()
	                .message(ex.getMessage())
	                .response(null)
	                .build();

	        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	    }
	

	
	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<ErrorResponse> name(InvalidPasswordException ex) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
	
	}

//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	public ResponseEntity<Map<String, String>> handleMethodNotArgsNotValidException(
//			MethodArgumentNotValidException ex) {
//		Map<String, String> respo = new HashMap<String, String>();
//		ex.getBindingResult().getAllErrors().forEach((error) -> {
//			String fieldName = ((FieldError) error).getField();
//			String message = error.getDefaultMessage();
//			respo.put(fieldName, message);
//		});
//
//		return new ResponseEntity<>(respo, HttpStatus.BAD_REQUEST);
//	}
	

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        String combinedMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + " (" + error.getDefaultMessage() + ")")
            .collect(Collectors.joining(", "));

        errors.put("message", "Validation failed: " + combinedMessage);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

	    // Generic handler for all other exceptions
	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
	        ApiResponse response = new ApiResponse(false, "An error occurred: " + ex.getMessage());
	        return ResponseEntity.internalServerError().body(response);
	    }
	    
	    
	    @ExceptionHandler(MissingServletRequestParameterException.class)
	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public Map<String, String> handleMissingParamsMap(MissingServletRequestParameterException ex) {
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", "Missing request parameter");
	        errorResponse.put("message", ex.getParameterName() + " parameter is missing");
	        return errorResponse;
	    }

	    
	    
	    @ExceptionHandler(UnauthorizedAccessException.class)
	    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedAccessException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
	    }

	    @ExceptionHandler(AccountDeactivatedException.class)
	    public ResponseEntity<ErrorResponse> handleAccountDeactivated(AccountDeactivatedException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
	    }

	    @ExceptionHandler(DateFormateException.class)
	    public ResponseEntity<ErrorResponse> handleDateFormat(DateFormateException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(DublicateEmailException.class)
	    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DublicateEmailException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT), HttpStatus.CONFLICT);
	    }

	    @ExceptionHandler(ConstraintViolationException.class)
	    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(OtpExpiredException.class)
	    public ResponseEntity<ErrorResponse> handleOtpExpired(OtpExpiredException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.GONE), HttpStatus.GONE);
	    }

	    @ExceptionHandler(OtpNotVerifiedException.class)
	    public ResponseEntity<ErrorResponse> handleOtpNotVerified(OtpNotVerifiedException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
	    }

	    @ExceptionHandler(InvalidStatusException.class)
	    public ResponseEntity<ErrorResponse> handleInvalidStatus(InvalidStatusException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(UserNotFoundException.class)
	    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	    }
	    
	    @ExceptionHandler(NotificationNotFoundException.class)
	    public ResponseEntity<ErrorResponse> handleUserNotFound(NotificationNotFoundException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	    }

	    @ExceptionHandler(EmptyOrNullFoundlException.class)
	    public ResponseEntity<ErrorResponse> handleEmptyOrNull(EmptyOrNullFoundlException ex) {
	        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	    }
	    @ExceptionHandler(UnauthorizedStatusUpdateException.class)
	    public ResponseEntity<String> handleUnauthorizedStatusUpdate(UnauthorizedStatusUpdateException ex) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
	    }
		
}
