package loggee.impl.def;

import javax.interceptor.InvocationContext;

import loggee.api.LogLineFormatter;


public class DefaultLogLineFormatter implements LogLineFormatter {
    public String format(String logMessage, InvocationContext invocationContext) {
        return logMessage;
    }
}
