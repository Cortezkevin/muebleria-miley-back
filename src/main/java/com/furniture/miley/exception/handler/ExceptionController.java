package com.furniture.miley.exception.handler;

import com.furniture.miley.commons.dto.ErrorResponseDTO;
import com.furniture.miley.exception.customexception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus( HttpStatus.NOT_FOUND )
    @ExceptionHandler( ResourceNotFoundException.class )
    public ErrorResponseDTO handlerResourceNotFoundException(ResourceNotFoundException resourceNotFoundException){
        return new ErrorResponseDTO(
                resourceNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( ConstraintViolationException.class )
    public ErrorResponseDTO handlerConstraintViolationException(ConstraintViolationException constraintViolationException){
        /*Map<String, String> errors = new HashMap<>();
        constraintViolationException.getConstraintViolations().forEach( error -> {
            String fieldName = error.getPropertyPath().toString().split("[.]")[2];
            errors.put(fieldName, error.getMessage());
        });*/
        return new ErrorResponseDTO(
                constraintViolationException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( MethodArgumentNotValidException.class )
    public ErrorResponseDTO handlerMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException){
        /*Map<String, String> errors = new HashMap<>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach( error -> {
            FieldError fieldError = ((FieldError) error);
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });*/
        return new ErrorResponseDTO(
                methodArgumentNotValidException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( NotMatchPasswordsException.class )
    public ErrorResponseDTO handlerNotMatchPasswordsException(NotMatchPasswordsException notMatchPasswordsException){
        return new ErrorResponseDTO(
                notMatchPasswordsException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( UnavailableUserException.class )
    public ErrorResponseDTO handlerUnavailableUserException(UnavailableUserException unavailableUserException){
        return new ErrorResponseDTO(
                unavailableUserException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( InvalidCredentialsException.class )
    public ErrorResponseDTO handlerInvalidCredentialsException(InvalidCredentialsException invalidCredentialsException){
        return new ErrorResponseDTO(
                invalidCredentialsException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( AbortedProcessException.class )
    public ErrorResponseDTO handlerAbortedProcessException(AbortedProcessException abortedProcessException){
        return new ErrorResponseDTO(
                abortedProcessException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( PrevStatusRequiredException.class )
    public ErrorResponseDTO handlerPrevStatusRequiredException(PrevStatusRequiredException prevStatusRequiredException){
        return new ErrorResponseDTO(
                prevStatusRequiredException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( AlreadyStartedProcessException.class )
    public ErrorResponseDTO handlerAlreadyStartedProcessException(AlreadyStartedProcessException alreadyStartedProcessException){
        return new ErrorResponseDTO(
                alreadyStartedProcessException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( FinishCurrentProcessException.class )
    public ErrorResponseDTO handlerFinishCurrentProcessException(FinishCurrentProcessException finishCurrentProcessException){
        return new ErrorResponseDTO(
                finishCurrentProcessException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( CannotCancelOrderException.class )
    public ErrorResponseDTO handlerCannotCancelOrderException(CannotCancelOrderException cannotCancelOrderException){
        return new ErrorResponseDTO(
                cannotCancelOrderException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( NotBelongCartException.class )
    public ErrorResponseDTO handlerNotBelongCartException(NotBelongCartException notBelongCartException){
        return new ErrorResponseDTO(
                notBelongCartException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }


    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( ProductAndMaterialNotFoundException.class )
    public ErrorResponseDTO handlerProductAndMaterialNotFoundException(ProductAndMaterialNotFoundException productAndMaterialNotFoundException){
        return new ErrorResponseDTO(
                productAndMaterialNotFoundException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( InsufficientStockException.class )
    public ErrorResponseDTO handlerInsufficientStockException(InsufficientStockException insufficientStockException){
        return new ErrorResponseDTO(
                insufficientStockException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( ResourceDuplicatedException.class )
    public ErrorResponseDTO handlerResourceDuplicatedException(ResourceDuplicatedException resourceDuplicatedException){
        return new ErrorResponseDTO(
                resourceDuplicatedException.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ResponseStatus( HttpStatus.UNAUTHORIZED )
    @ExceptionHandler( MalformedJwtException.class )
    public ErrorResponseDTO handlerMalformedJwtException(MalformedJwtException malformedJwtException){
        return new ErrorResponseDTO(
                "Token mal formado",
                HttpStatus.UNAUTHORIZED.name()
        );
    }

    @ResponseStatus( HttpStatus.UNAUTHORIZED )
    @ExceptionHandler( SignatureException.class )
    public ErrorResponseDTO handleSignatureException(SignatureException SignatureException){
        return new ErrorResponseDTO(
                "Token con firma invalida",
                HttpStatus.UNAUTHORIZED.name()
        );
    }

    @ResponseStatus( HttpStatus.UNAUTHORIZED )
    @ExceptionHandler( WeakKeyException.class )
    public ErrorResponseDTO handlerWeakKeyException(WeakKeyException weakKeyException){
        return new ErrorResponseDTO(
                "Token mal firmado",
                HttpStatus.UNAUTHORIZED.name()
        );
    }

    @ResponseStatus( HttpStatus.UNAUTHORIZED )
    @ExceptionHandler( ExpiredJwtException.class )
    public ErrorResponseDTO handlerExpiredJwtException(ExpiredJwtException expiredJwtException){
        return new ErrorResponseDTO(
                "Token expirado",
                HttpStatus.UNAUTHORIZED.name()
        );
    }

    @ResponseStatus( HttpStatus.FORBIDDEN )
    @ExceptionHandler( AccessDeniedException.class )
    public ErrorResponseDTO handlerAccessDeniedException(AccessDeniedException accessDeniedException){
        return new ErrorResponseDTO(
                "No tiene los permisos para realizar esta accion",
                HttpStatus.FORBIDDEN.name()
        );
    }

    @ResponseStatus( HttpStatus.INTERNAL_SERVER_ERROR )
    @ExceptionHandler( RuntimeException.class )
    public ErrorResponseDTO handle(RuntimeException runtimeException){
        return new ErrorResponseDTO(
                "Error desconocido: " + runtimeException.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );
    }
}

