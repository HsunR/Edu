package com.gpnu.common.utils.validator;

import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 校验工具类：用于手动触发校验并统一处理异常
 */
@Component
public class ValidationUtil {

    private static Validator validator;

    @Autowired
    public ValidationUtil(Validator validator) {
        ValidationUtil.validator = validator;
    }

    /**
     * 手动校验并抛出业务异常
     */
    public static <T> void validateAndThrow(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                errorMessage.append(violation.getMessage()).append("；");
            }
            throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMessage.toString());
        }
    }
}
