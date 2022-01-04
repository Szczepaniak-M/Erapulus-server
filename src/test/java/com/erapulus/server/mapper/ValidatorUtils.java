package com.erapulus.server.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatorUtils {

    public static List<String> getValidationResult(Object result) {
        return createValidator()
                .validate(result)
                .stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .toList();
    }

    public static Validator createValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
