package loggee.impl;

import javax.inject.Inject;
import javax.interceptor.InvocationContext;

import loggee.api.EMethodLogPolicy;
import loggee.api.Logged;
import loggee.api.MethodLogger;
import loggee.dependent.slf4j.api.ELogLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodLoggerImpl implements MethodLogger {
    private static final String METHOD_CALL_BEGINNING_LOG_MARKER = "STARTED ";
    private static final String METHOD_CALL_ENDING_LOG_MARKER = "FINISHED ";

    @Inject
    protected LogHelper logHelper;

    public Object log(InvocationContext ctx, Logged loggedAnnotation) throws Exception {
        Object returnValue;

        Class<?> returnType = ctx.getMethod().getReturnType();
        if (shouldLogAsDecision(loggedAnnotation, returnType)) {
            returnValue = logDecisionMethod(ctx, loggedAnnotation);
        } else {
            returnValue = logRegularMethod(ctx, loggedAnnotation);
        }

        return returnValue;
    }

    private Object logRegularMethod(InvocationContext ctx, Logged loggedAnnotation) throws Exception {
        LogData logData = logHelper.getLogData(ctx);
        String targetClassName = logData.getTargetClassName();
        String methodInvocationAsString = logData.getMethodInvocationAsString();

        ELogLevel logLevel = loggedAnnotation.logLevel();
        ELogLevel parameterLogLevel = loggedAnnotation.parameterLogLevel();
        int trim = loggedAnnotation.trim();

        Logger baseLogger = LoggerFactory.getLogger(logHelper.buildLoggerName(loggedAnnotation.regularMethodLoggerBaseName(), targetClassName));

        Logger parameterLogger = LoggerFactory.getLogger(logHelper.buildLoggerName(loggedAnnotation.parameterLoggerBaseName(),
                loggedAnnotation.regularMethodLoggerBaseName(), targetClassName));

        long start = System.currentTimeMillis();
        try {
            boolean logMethodParametersAfterCall = loggedAnnotation.logMethodParametersAfterCall();

            String preMethodCallLogPrefix;
            String postMethodCallLogPrefix;
            if (logMethodParametersAfterCall) {
                preMethodCallLogPrefix = METHOD_CALL_BEGINNING_LOG_MARKER;
                postMethodCallLogPrefix = METHOD_CALL_ENDING_LOG_MARKER;
            } else {
                preMethodCallLogPrefix = "";
                postMethodCallLogPrefix = "";
            }

            logHelper.log(logLevel, baseLogger, METHOD_CALL_BEGINNING_LOG_MARKER + methodInvocationAsString, ctx);
            logHelper.logParameters(parameterLogLevel, parameterLogger, preMethodCallLogPrefix + methodInvocationAsString, ctx.getParameters(), trim, ctx);

            Object result = ctx.proceed();

            long durationInMs = logHelper.calculateDuration(start);

            logHelper
                    .log(logLevel,
                            baseLogger,
                            METHOD_CALL_ENDING_LOG_MARKER + methodInvocationAsString + " in " + durationInMs + "ms : "
                                    + logHelper.getObjectAsString(result, trim), ctx);
            if (logMethodParametersAfterCall) {
                logHelper.logParameters(parameterLogLevel, parameterLogger, postMethodCallLogPrefix + methodInvocationAsString, ctx.getParameters(), trim, ctx);
            }

            return result;
        } catch (Exception e) {
            long durationInMs = logHelper.calculateDuration(start);

            ELogLevel failureLogLevel = loggedAnnotation.failureLogLevel();

            logHelper.log(failureLogLevel, baseLogger, "FAILED " + methodInvocationAsString + " in " + durationInMs + "ms", ctx);

            throw e;
        }
    }

    private Object logDecisionMethod(InvocationContext ctx, Logged loggedAnnotation) throws Exception {
        LogData logData = logHelper.getLogData(ctx);
        String targetClassName = logData.getTargetClassName();
        String methodInvocationAsString = logData.getMethodInvocationAsString();

        ELogLevel logLevel = loggedAnnotation.logLevel();
        ELogLevel parameterLogLevel = loggedAnnotation.parameterLogLevel();
        int trim = loggedAnnotation.trim();

        Logger baseLogger = LoggerFactory.getLogger(logHelper.buildLoggerName(loggedAnnotation.decisionMethodLoggerBaseName(), targetClassName));

        Logger parameterLogger = LoggerFactory.getLogger(logHelper.buildLoggerName(loggedAnnotation.decisionParameterLoggerBaseName(),
                loggedAnnotation.decisionMethodLoggerBaseName(), targetClassName));

        Object outcome = ctx.proceed();

        logHelper.log(logLevel, baseLogger, methodInvocationAsString + " " + logHelper.getObjectAsString(outcome, trim), ctx);

        logHelper.logParameters(parameterLogLevel, parameterLogger, methodInvocationAsString, ctx.getParameters(), trim, ctx);

        return outcome;
    }

    private boolean shouldLogAsDecision(Logged loggedAnnotation, Class<?> returnType) {
        return EMethodLogPolicy.DECISION.equals(loggedAnnotation.booleanMethodLogPolicy())
                && (Boolean.class.equals(returnType) || boolean.class.equals(returnType));
    }
}
