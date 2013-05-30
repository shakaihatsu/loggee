package loggee.api;

import javax.interceptor.InvocationContext;

public interface LogLineFormatter {
    String format(String logMessage, InvocationContext invocationContext);
}
