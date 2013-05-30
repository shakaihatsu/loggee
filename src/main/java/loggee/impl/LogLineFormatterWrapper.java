package loggee.impl;

import javax.interceptor.InvocationContext;

import loggee.api.LogLineFormatter;
import loggee.impl.spec.IWrapper;


public class LogLineFormatterWrapper implements IWrapper<LogLineFormatter> {
    private LogLineFormatter logLineFormatter;

    public void setDelegate(LogLineFormatter delegate) {
        setLogLineFormatter(delegate);
    }

    public String format(String logMessage, InvocationContext invocationContext) {
        return logLineFormatter.format(logMessage, invocationContext);
    }

    public LogLineFormatter getLogLineFormatter() {
        return logLineFormatter;
    }

    public void setLogLineFormatter(LogLineFormatter logLineFormatter) {
        this.logLineFormatter = logLineFormatter;
    }

    @Override
    public String toString() {
        return "LogLineFormatterWrapper [logLineFormatter=" + logLineFormatter + "]";
    }

}
