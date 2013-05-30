package loggee.api;

import javax.interceptor.InvocationContext;

public interface MethodLogger {
    Object log(InvocationContext ctx, Logged loggedAnnotation) throws Exception;
}
