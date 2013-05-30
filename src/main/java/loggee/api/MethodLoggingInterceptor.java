package loggee.api;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Logged
@Interceptor
public class MethodLoggingInterceptor {
    @Inject
    protected MethodLogger methodLogger;

    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        Logged loggedAnnotation = getLoggedAnnotation(ctx);

        return methodLogger.log(ctx, loggedAnnotation);
    }

    private Logged getLoggedAnnotation(InvocationContext ctx) {
        Logged loggedAnnotation = ctx.getMethod().getAnnotation(Logged.class);

        if (loggedAnnotation == null) {
            loggedAnnotation = ctx.getTarget().getClass().getAnnotation(Logged.class);
        }

        if (loggedAnnotation == null) {
            throw new IllegalStateException();
        }

        return loggedAnnotation;
    }
}
