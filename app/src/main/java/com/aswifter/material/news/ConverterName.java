package com.aswifter.material.news;

/**
 * Created by erfli on 6/18/16.
 */

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface ConverterName
{
    String value() default "gson";
}
