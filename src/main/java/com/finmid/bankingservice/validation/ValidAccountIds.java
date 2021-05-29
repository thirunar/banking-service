package com.finmid.bankingservice.validation;

import com.finmid.bankingservice.dto.TransactionDto;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CreditDebitAccountValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccountIds {
    String message() default "From and To Account Ids should not be the same";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class CreditDebitAccountValidator implements ConstraintValidator<ValidAccountIds, TransactionDto> {
    public void initialize(ValidAccountIds constraintAnnotation) {

    }

    @Override
    public boolean isValid(TransactionDto transactionDto, ConstraintValidatorContext constraintValidatorContext) {

        return !transactionDto.getFromAccountId().equals(transactionDto.getToAccountId());
    }
}