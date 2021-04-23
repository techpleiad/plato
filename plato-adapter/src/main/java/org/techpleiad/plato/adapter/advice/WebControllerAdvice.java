package org.techpleiad.plato.adapter.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.techpleiad.plato.adapter.exception.ErrorResponse;
import org.techpleiad.plato.api.exceptions.InvalidRuleException;
import org.techpleiad.plato.core.exceptions.BranchNotSupportedException;
import org.techpleiad.plato.core.exceptions.FileConvertException;
import org.techpleiad.plato.core.exceptions.FileDeleteException;
import org.techpleiad.plato.core.exceptions.GitBranchNotFoundException;
import org.techpleiad.plato.core.exceptions.GitRepositoryNotFoundException;
import org.techpleiad.plato.core.exceptions.ServiceAlreadyExistException;
import org.techpleiad.plato.core.exceptions.ServiceNotFoundException;
import org.techpleiad.plato.core.exceptions.ServicesNotFoundException;
import org.techpleiad.plato.core.exceptions.ValidationRuleAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class WebControllerAdvice {

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String BRANCHES = "branches";
    private static final String SERVICE = "service";
    private static final String VALIDATION_RULE = "validationRule";
    private static final String URL = "url";
    private static final String VALIDATION_RULE_PROPERTY = "validationRuleProperty";
    private static final String ERROR_IN_JSON_SCHEMA = "errorInJsonSchema";


    @ExceptionHandler(value = GitRepositoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> generateGitNotFoundException(final GitRepositoryNotFoundException exception) {

        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());
        error.put(URL, exception.getUrl());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = GitBranchNotFoundException.class)
    public ResponseEntity<ErrorResponse> generateGitBranchNotFoundException(final GitBranchNotFoundException exception) {

        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());

        error.put(BRANCHES, exception.getBranches());
        error.put(URL, exception.getUrl());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BranchNotSupportedException.class)
    public ResponseEntity<ErrorResponse> generateBranchNotSupportedException(final BranchNotSupportedException exception) {

        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());
        error.put(BRANCHES, exception.getBranches());
        error.put(SERVICE, exception.getService());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ServiceAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> generateServiceExistException(final ServiceAlreadyExistException exception) {

        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());
        error.put(SERVICE, exception.getId());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ValidationRuleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> generateValidationRuleExistsException(final ValidationRuleAlreadyExistsException exception) {

        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());
        error.put(VALIDATION_RULE_PROPERTY, exception.getRuleOnProperty());
        error.put(VALIDATION_RULE, exception.getRule());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidRuleException.class)
    public ResponseEntity<ErrorResponse> generateInvalidRuleException(final InvalidRuleException exception) {
        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());
        error.put(ERROR_IN_JSON_SCHEMA, exception.getDetailMessage());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ServiceNotFoundException.class)
    public ResponseEntity<ErrorResponse> generateServiceNotFoundException(final ServiceNotFoundException exception) {

        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());
        error.put(SERVICE, exception.getService());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> generateException(final Exception exception) {

        if (exception.getCause() != null && exception.getCause().getClass().equals(InvalidRuleException.class)) {
            return generateInvalidRuleException((InvalidRuleException) exception.getCause());
        }
        final HashMap<String, Object> error = new HashMap<>();
        final Throwable exp = exception.getCause();

        error.put(ERROR_MESSAGE, Objects.nonNull(exp) ? exp.getMessage() : exception.getMessage());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = FileDeleteException.class)
    public ResponseEntity<ErrorResponse> generateFileUtilsDeleteFail(final FileDeleteException exception) {

        final HashMap<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());
        error.put("file", exception.getFilePath());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ServicesNotFoundException.class)
    public ResponseEntity<ErrorResponse> generateServicesNotFoundException(final ServicesNotFoundException exception) {

        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());
        error.put(SERVICE, exception.getServiceList());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = FileConvertException.class)
    public ResponseEntity<ErrorResponse> generateFileConvertException(final FileConvertException exception) {

        final Map<String, Object> error = new HashMap<>();
        error.put(ERROR_MESSAGE, exception.getErrorMessage());

        return new ResponseEntity<>(new ErrorResponse(error, HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
