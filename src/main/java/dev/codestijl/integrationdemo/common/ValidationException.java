package dev.codestijl.integrationdemo.common;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

/**
 * Exception thrown when an entity fails validation.
 *
 * @author darren
 * @since 1.0.0
 */
@Getter
public class ValidationException extends Exception {

    private static final long serialVersionUID = -8492440004188899807L;

    private final List<String> errors = new LinkedList<>();

    /**
     * Constructs a new ValidationException.
     *
     * @param message The message for the exception.
     * @param errors The specific validation errors.
     */
    public ValidationException(final String message, final Collection<String> errors) {
        super(message);
        this.errors.addAll(errors);
    }

    /**
     * Constructs a new ValidationException with a single error.
     *
     * @param message The message for the exception.
     * @param error The validation error.
     */
    public ValidationException(final String message, final String error) {
        this(message, List.of(error));
    }
}
