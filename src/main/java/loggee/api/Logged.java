package loggee.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import loggee.dependent.slf4j.api.ELogLevel;


@InterceptorBinding
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Logged {
    @Nonbinding
    ELogLevel logLevel() default ELogLevel.DEBUG;

    @Nonbinding
    ELogLevel parameterLogLevel() default ELogLevel.DEBUG;

    @Nonbinding
    ELogLevel failureLogLevel() default ELogLevel.WARN;

    @Nonbinding
    String regularMethodLoggerBaseName() default "METHOD_CALL";

    @Nonbinding
    String decisionMethodLoggerBaseName() default "DECISION";

    @Nonbinding
    String parameterLoggerBaseName() default "PARAMETER";

    @Nonbinding
    EMethodLogPolicy booleanMethodLogPolicy() default EMethodLogPolicy.DECISION;

    @Nonbinding
    boolean logMethodParametersAfterCall() default false;

    @Nonbinding
    int trim() default 500;
}
