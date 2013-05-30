package loggee.impl;

import javax.enterprise.inject.spi.InjectionPoint;

import loggee.api.LoggerProducer;
import loggee.impl.spec.IWrapper;

import org.slf4j.Logger;


public class LoggerProducerWrapper implements IWrapper<LoggerProducer> {
    private LoggerProducer loggerProducer;

    public Logger produceLogger(InjectionPoint injectionPoint) {
        return loggerProducer.produceLogger(injectionPoint);
    }

    public void setDelegate(LoggerProducer delegate) {
        setLoggerProducer(delegate);
    }

    public LoggerProducer getLoggerProducer() {
        return loggerProducer;
    }

    public void setLoggerProducer(LoggerProducer loggerProducer) {
        this.loggerProducer = loggerProducer;
    }

    @Override
    public String toString() {
        return "LoggerProducerWrapper [loggerProducer=" + loggerProducer + "]";
    }

}
