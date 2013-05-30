package loggee.impl.def;

import javax.enterprise.inject.spi.InjectionPoint;

import loggee.api.LoggerProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultLoggerProducer implements LoggerProducer {
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}
