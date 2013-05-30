package loggee.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;

import loggee.api.LogLineFormatter;
import loggee.dependent.slf4j.api.ELogLevel;
import loggee.dependent.slf4j.impl.Slf4JMessageLogger;
import loggee.impl.def.DefaultLogLineFormatter;
import loggee.impl.spec.ModulePrivate;

import org.slf4j.Logger;


@ApplicationScoped
public class LogHelper {
    public static final String LOGGER_DELIMITER = ".";

    @Inject
    private Logger logger;

    @Inject
    @ModulePrivate
    private LogLineFormatterWrapper logLineFormatter;
    @Inject
    @Any
    private Instance<LogLineFormatter> logLineFormatterImplementations;
    @Inject
    private DefaultLogLineFormatter defaultLogLineFormatter;

    @Inject
    private Slf4JMessageLogger slf4JMessageLogger;
    @Inject
    private LogUtil logUtil;

    LogData getLogData(InvocationContext ctx) {
        LogData logData;

        Class<? extends Object> targetClass = ctx.getTarget().getClass();
        String targetClassName = getPurifiedClassName(targetClass.getName());
        String targetClassSimpleName = getPurifiedClassName(targetClass.getSimpleName());
        String methodName = ctx.getMethod().getName();
        String methodInvocationAsString = targetClassSimpleName + "." + methodName;

        logData = new LogData();
        logData.setTargetClassName(targetClassName);
        logData.setTargetClassSimpleName(targetClassSimpleName);
        logData.setMethodName(methodName);
        logData.setMethodInvocationAsString(methodInvocationAsString);

        return logData;
    }

    String buildLoggerName(String ... loggerNames) {
        StringBuilder loggerNameBuilder;

        if (loggerNames != null && loggerNames.length > 0) {
            List<String> loggerNameList = new LinkedList<String>(Arrays.asList(loggerNames));

            loggerNameBuilder = new StringBuilder(loggerNameList.remove(0));
            for (String loggerName : loggerNameList) {
                loggerNameBuilder.append(LOGGER_DELIMITER).append(loggerName);
            }
        } else {
            throw new NullPointerException();
        }

        return loggerNameBuilder.toString();
    }

    void log(ELogLevel logLevel, Logger logger, String logMessage, InvocationContext invocationContext) {
        slf4JMessageLogger.logFormattedMessage(logLevel, logger, logLineFormatter.format(logMessage, invocationContext));
    }

    void logParameters(ELogLevel logLevel, Logger parameterLogger, String methodName, Object[] parameters, int trim, InvocationContext invocationContext) {
        int parameterIndex = 0;
        for (Object parameter : parameters) {
            log(logLevel, parameterLogger, methodName + " " + parameterIndex + " : " + getObjectAsString(parameter, trim), invocationContext);

            parameterIndex++;
        }
    }

    String getPurifiedClassName(String className) {
        String purifiedClassName;

        int endIndex = className.indexOf("$");
        if (endIndex > -1) {
            purifiedClassName = className.substring(0, endIndex);
        } else {
            purifiedClassName = className;
        }

        return purifiedClassName;
    }

    long calculateDuration(long start) {
        long end = System.currentTimeMillis();
        long durationInMs = end - start;
        return durationInMs;
    }

    String getObjectAsString(Object object, int trim) {
        String objectAsString;

        if (object == null)
            return "NULL";

        try {
            objectAsString = object.toString();

            if (trim > 0) {
                String trimWithoutLeadingAndTrailingWhitespaces = objectAsString.trim();

                if (trimWithoutLeadingAndTrailingWhitespaces.length() > trim) {
                    trimWithoutLeadingAndTrailingWhitespaces.substring(0, trim);
                }
            }
        } catch (Exception e) {
            logger.warn("Couldn't invoke toString() on object", e);
            objectAsString = "N/A";
        }

        return objectAsString;
    }

    @Produces
    @ModulePrivate
    @ApplicationScoped
    LogLineFormatterWrapper selectUsedLogLineFormatterImplementation() {
        LogLineFormatterWrapper usedImplementation;

        usedImplementation = logUtil.selectUsedImplementation(LogLineFormatter.class, logLineFormatterImplementations, new LogLineFormatterWrapper(),
                defaultLogLineFormatter);

        return usedImplementation;
    }
}
