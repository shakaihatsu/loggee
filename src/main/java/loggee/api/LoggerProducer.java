package loggee.api;

import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;

public interface LoggerProducer {
    Logger produceLogger(InjectionPoint injectionPoint);
}
