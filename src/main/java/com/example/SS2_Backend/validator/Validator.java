package com.example.SS2_Backend.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public @interface Validator {

    @Target({ElementType.FIELD})
    @Retention(RUNTIME)
    @interface ContainedFromList {

        /**
         * values
         *
         * @return valid values
         */
        String[] values() default "";

        /**
         * reject message
         * @return String message
         */
        String message() default "";

        /**
         * allow blank field value
         * @return boolean allowBlank default true
         */
        boolean allowBlank() default true;

    }

}
