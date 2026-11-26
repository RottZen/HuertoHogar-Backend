package cl.huertohogar.pedido.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        int codigo = ex.getStatusCode().value();
        HttpStatus status = HttpStatus.resolve(codigo);
        String mensaje = ex.getReason() != null ? ex.getReason()
                : (status != null ? status.getReasonPhrase() : "Error");
        return build(codigo, mensaje, req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(fe.getField(),
                    fe.getDefaultMessage() == null ? "Valor invalido" : fe.getDefaultMessage());
        }
        return build(400, "Datos invalidos: revisa los campos del formulario", req, errores);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> errores.putIfAbsent(v.getPropertyPath().toString(), v.getMessage()));
        return build(400, "Datos invalidos", req, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(400, "El cuerpo de la peticion no es un JSON valido o tiene datos de tipo incorrecto", req, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return build(400, "Falta el parametro obligatorio '" + ex.getParameterName() + "'", req, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String tipo = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valido";
        return build(400, "El parametro '" + ex.getName() + "' debe ser de tipo " + tipo, req, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violacion de integridad en {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(409, "Ya existe un registro con esos datos o algun valor no es valido", req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(405, "El metodo " + ex.getMethod() + " no esta permitido en esta ruta", req, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return build(415, "Formato no soportado. Envia el cuerpo como application/json", req, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        return build(404, "La ruta " + req.getRequestURI() + " no existe", req, null);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> handleRestClient(RestClientException ex, HttpServletRequest req) {
        log.error("Fallo al llamar a otro microservicio", ex);
        return build(502, "No se pudo comunicar con otro servicio. Verifica que producto-service y usuario-service esten arriba", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        if (ex instanceof ErrorResponse errorResponse) {
            String detalle = errorResponse.getBody().getDetail();
            return build(errorResponse.getStatusCode().value(), detalle != null ? detalle : "Solicitud invalida", req, null);
        }
        log.error("Error inesperado en {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(500, "Error interno del servidor. Intenta nuevamente en unos minutos", req, null);
    }

    private ResponseEntity<ApiError> build(int codigo, String mensaje, HttpServletRequest req, Map<String, String> errores) {
        HttpStatus status = HttpStatus.resolve(codigo);
        String nombre = status != null ? status.getReasonPhrase() : "Error";
        ApiError body = new ApiError(LocalDateTime.now(), codigo, nombre, mensaje, req.getRequestURI(), errores);
        return ResponseEntity.status(codigo).body(body);
    }
}
