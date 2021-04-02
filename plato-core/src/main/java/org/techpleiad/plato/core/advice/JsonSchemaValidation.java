package org.techpleiad.plato.core.advice;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JsonSchemaValidationAspect.class)
public @interface JsonSchemaValidation {
    String message() default "Invalid json schema";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
