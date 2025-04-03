package com.simplesdental.product.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

public class LogUtils {
    
    public static void logDebug(String message, String className, Map<String, Object> context) {
        Logger logger = LoggerFactory.getLogger(className);
        try {
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            logger.debug(message);
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
        }
    }

    public static void logInfo(String message, String className, Map<String, Object> context) {
        Logger logger = LoggerFactory.getLogger(className);
        try {
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            logger.info(message);
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
        }
    }

    public static void logWarning(String message, String className, Map<String, Object> context) {
        Logger logger = LoggerFactory.getLogger(className);
        try {
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            logger.warn(message);
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
        }
    }

    public static void logError(String message, String className, Throwable error, Map<String, Object> context) {
        Logger logger = LoggerFactory.getLogger(className);
        try {
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            if (error != null) {
                MDC.put("errorType", error.getClass().getSimpleName());
                MDC.put("errorMessage", error.getMessage());
                logger.error(message, error);
            } else {
                logger.error(message);
            }
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
            MDC.remove("errorType");
            MDC.remove("errorMessage");
        }
    }

    public static void logCritical(String message, String className, Throwable error, Map<String, Object> context) {
        Logger logger = LoggerFactory.getLogger(className);
        try {
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            if (error != null) {
                MDC.put("errorType", error.getClass().getSimpleName());
                MDC.put("errorMessage", error.getMessage());
                logger.error("CRITICAL: " + message, error);
            } else {
                logger.error("CRITICAL: " + message);
            }
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
            MDC.remove("errorType");
            MDC.remove("errorMessage");
        }
    }
}
