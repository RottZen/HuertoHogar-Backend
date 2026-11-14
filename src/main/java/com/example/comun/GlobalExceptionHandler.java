package com.example.comun;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleInvalidBody(
            MethodArgumentNotValidException excepcion, HttpServletRequest solicitud) {
        Map<String, String> erroresValidacion = new LinkedHashMap<>();
        excepcion.getBindingResult().getFieldErrors()
                .forEach(errorValidacion -> erroresValidacion.putIfAbsent(
                        errorValidacion.getField(), errorValidacion.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "La solicitud contiene datos no válidos.", solicitud, erroresValidacion);
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorApi> handleInvalidParameter(Exception excepcion, HttpServletRequest solicitud) {
        return error(HttpStatus.BAD_REQUEST, "Uno o más parámetros no son válidos.", solicitud, Map.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> handleStatus(ResponseStatusException excepcion, HttpServletRequest solicitud) {
        HttpStatus estadoHttp = HttpStatus.valueOf(excepcion.getStatusCode().value());
        return error(estadoHttp, excepcion.getReason(), solicitud, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException excepcion, HttpServletRequest solicitud) {
        return error(HttpStatus.CONFLICT, "Ya existe un registro con uno de los valores indicados.", solicitud, Map.of());
    }

    private ResponseEntity<ApiError> error(
            HttpStatus estadoHttp, String mensaje, HttpServletRequest solicitud, Map<String, String> erroresValidacion) {
        ErrorApi cuerpo = new ErrorApi(
                Instant.now(), estadoHttp.value(), statusReasonInSpanish(estadoHttp), mensaje,
                solicitud.getRequestURI(), erroresValidacion);
        return ResponseEntity.status(estadoHttp).body(cuerpo);
    }

    private String statusReasonInSpanish(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Solicitud incorrecta";
            case UNAUTHORIZED -> "No autorizado";
            case FORBIDDEN -> "Acceso denegado";
            case NOT_FOUND -> "No encontrado";
            case CONFLICT -> "Conflicto";
            case TOO_MANY_REQUESTS -> "Demasiadas solicitudes";
            case INTERNAL_SERVER_ERROR -> "Error interno del servidor";
            default -> "Error";
        };
    }
}

