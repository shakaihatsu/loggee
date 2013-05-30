package loggee.dependent.slf4j.impl;

import loggee.dependent.slf4j.api.ELogLevel;

import org.slf4j.Logger;


public class Slf4JMessageLogger {
    public void logFormattedMessage(ELogLevel level, Logger logger, String formattedMessage) {
        if (level == ELogLevel.TRACE) {
            logger.trace(formattedMessage);
        } else if (level == ELogLevel.DEBUG) {
            logger.debug(formattedMessage);
        } else if (level == ELogLevel.INFO) {
            logger.info(formattedMessage);
        } else if (level == ELogLevel.WARN) {
            logger.warn(formattedMessage);
        } else if (level == ELogLevel.ERROR) {
            logger.error(formattedMessage);
        }
    }
}
