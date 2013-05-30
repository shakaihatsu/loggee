package loggee.impl;

import java.util.Iterator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import loggee.api.LoggerProducer;
import loggee.impl.def.DefaultLoggerProducer;
import loggee.impl.spec.IWrapper;
import loggee.impl.spec.ModulePrivate;

import org.slf4j.Logger;


@ApplicationScoped
public class LogUtil {
    @Inject
    @ModulePrivate
    private LoggerProducerWrapper loggerProducer;
    @Inject
    @Any
    private Instance<LoggerProducer> loggerProducerImplementations;
    @Inject
    private DefaultLoggerProducer defaultLoggerProducer;

    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return loggerProducer.produceLogger(injectionPoint);
    }

    @Produces
    @ModulePrivate
    @ApplicationScoped
    LoggerProducerWrapper selectUsedLoggerProducerImplementation() {
        LoggerProducerWrapper usedImplementation;

        usedImplementation = selectUsedImplementation(LoggerProducer.class, loggerProducerImplementations, new LoggerProducerWrapper(), defaultLoggerProducer);

        return usedImplementation;
    }

    <C, W extends IWrapper<C>, D extends C> W selectUsedImplementation(Class<C> interfaceClass, Instance<C> instances, W newWrapper, D defaultImplementation) {
        W wrapper;

        Iterator<C> instancesIterator = instances.iterator();

        C instance = defaultImplementation;
        while(instancesIterator.hasNext()) {
            C currentInstance = instancesIterator.next();

            Class<? extends Object> classOfDefaultImplementation = defaultImplementation.getClass();
            Class<? extends Object> classOfInstance = instance.getClass();
            Class<? extends Object> classOfCurrentInstance = currentInstance.getClass();

            if (classOfInstance.equals(classOfDefaultImplementation)) {
                instance = currentInstance;
            } else if (!classOfCurrentInstance.equals(classOfDefaultImplementation)) {
                throw new IllegalStateException("Multiple bean definition for " + interfaceClass + "!");
            }
        }

        wrapper = newWrapper;
        wrapper.setDelegate(instance);

        return wrapper;
    }
}
