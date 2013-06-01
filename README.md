LoggEE
======

Automatic Logging Solution For Java EE Applications

Quick view
----------
```
12:00:00,001 DEBUG [METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) STARTED MyClass.doSomething
12:00:00,001 DEBUG [PARAMETER.METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) MyClass.doSomething 0 : some_string
12:00:00,001 DEBUG [PARAMETER.METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) MyClass.doSomething 1 : 10
12:00:00,001 DEBUG [METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) FINISHED MyClass.doSomething in 2ms : java.lang.Object@1d0688bf
12:00:00,001 DEBUG [DECISION.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) MyClass.isItOkay true
12:00:00,001 DEBUG [PARAMETER.DECISION.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) MyClass.isItOkay 0 : some_other_string
12:00:00,001 DEBUG [METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) STARTED MyClass.failingMethod
12:00:00,001 WARN  [METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) FAILED MyClass.failingMethod in 0ms
Stacktrace here
```

Overview
--------

LoggEE is a simple, easy-to-use, highly customizable automatic logging module which can significantly lower the number
of log statements sprinkled throughout the code by automatically logging method calls using AOP techniques.

Using CDI itself, the current implementation of LoggEE supports only other applications that also use CDI.
LoggEE uses slf4j as the backing logging framework.

How to install
--------------

LoggEE is a Maven project. Once it has been cloned, it can be installed into any Maven repository and then added
as a dependency into any other Maven project.

How to use
----------

1. Add LoggEE as a Maven dependency
2. Add a provider for _slf4j_ as a Maven dependency, unless your container (e.g. application server) does this for you
3. Add ```loggee.api.LoggeeInterceptor``` to your ```beans.xml``` as an interceptor, like this:
```xml
<?xml version="1.0"?>
<beans xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd">
  
      <interceptors>
        <class>loggee.api.LoggeeInterceptor</class>
      </interceptors>
</beans>
```
4. Annotate your class or your method with ```loggee.api.Logged```, like this:
  ```java
  @Logged
  public class MyClass {
  ...

  or

  public class MyClass {
      @Logged
      public void myMethod() {
  ```

The output
----------

LoggEE actually logs method calls, handling boolean (referenced as _"decision"_) and other (referenced as _"regular"_)
methods differently:
  - Regular methods
    1. __Start__ of the method
    2. __Parameters__
    3. __End__ of the method, the __return value__ and the __duration__
    4. __Parameters__ again (they may have changed during the method call) - This is optional and OFF by default
      (see [Configuration](#configuration))
  - Decision methods
    1. __Return value__
    2. __Parameters__
    
By default, decision methods are logged only after they are called. This behaviour can be changed, telling LoggEE to
consider a boolean method (or all the boolean methods of a class, depending where you put the _@Logged_ annotation)
as _"regular"_ (see [Configuration](#configuration)).

Example output:

```
12:00:00,001 DEBUG [METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) STARTED MyClass.doSomething
12:00:00,001 DEBUG [PARAMETER.METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) MyClass.doSomething 0 : some_string
12:00:00,001 DEBUG [PARAMETER.METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) MyClass.doSomething 1 : 10
12:00:00,001 DEBUG [METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) FINISHED MyClass.doSomething in 2ms : java.lang.Object@1d0688bf
12:00:00,001 DEBUG [DECISION.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) MyClass.isItOkay true
12:00:00,001 DEBUG [PARAMETER.DECISION.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) MyClass.isItOkay 0 : some_other_string
12:00:00,001 DEBUG [METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) STARTED MyClass.failingMethod
12:00:00,001 WARN  [METHOD_CALL.fully.qualified.name.of.MyClass] (http-localhost-127.0.0.1-8080-1) FAILED MyClass.failingMethod in 0ms
Stacktrace here
```

The log lines are prefixed with __METHOD_CALL__, __PARAMETER.METHOD_CALL__, __DECISION__ or __PARAMETER.DECISION__,
depending what is being logged. This is to help configure logging for these kind of logs universally. You can make
LoggEE to omit this prefixes if you want (see [Configuration](#configuration)).

Additional features
-------------------

LoggEE also provides ```org.slf4j.Logger``` loggers, so you can simply inject them into your managed beans like this:

```java
import javax.inject.Inject;
import org.slf4j.Logger;

public class MyClass {
    @Inject
    private Logger logger;
```

(Please keep in mind that this may be a restriction as well, in case you want to make your own producer of
```org.slf4j.Logger``` instances. For this reason, LoggEE can be configured how to produce ```org.slf4j.Logger```
instances. See [Configuration](#configuration).)

Configuration
-------------

There are a couple of ways to configure LoggEE:

1. Configure the ```loggee.api.Logged``` annotation
  - _logLevel_ - Log level for regular methods - Default: __DEBUG__
  - _parameterLogLevel_ - Log level for parameters - Default: __DEBUG__
  - _failureLogLevel_ - Log level for when the method throws an exception - Default: __WARN__
  - _regularMethodLoggerBaseName_ - Logger base name for regular methods - Default: __"METHOD_CALL"__
      - Setting this to "" (empty string) will make LoggEE use the original logger.
  - _decisionMethodLoggerBaseName_ - Logger base name for decision methods - Default: __"DECISION"__
      - Setting this to "" (empty string) will make LoggEE use the original logger.
  - _parameterLoggerBaseName_ - Logger base name for parameters of regular methods - Default: __"PARAMETER"__
      - Setting this to "" (empty string) will make LoggEE use the regular method logger.
      - Setting both to "" (empty string) will make LoggEE use the original logger.
  - _decisionParameterLoggerBaseName_ - Logger base name for parameters of decision methods - Default: __"PARAMETER"__
      - Setting this to "" (empty string) will make LoggEE use the decision method logger.
      - Setting both to "" (empty string) will make LoggEE use the original logger.
  - _booleanMethodLogPolicy_ - How to treat boolean methods - Default: __DECISION__
  - _logMethodParametersAfterCall_ - Log regular method parameters after the call again? - Default: __false__
  - _trim_ - Trim any log message to this number of characters - Default: __500__ (Doesn't trim if set to 0)
2. Define own ```loggee.api.LogLineFormatter```.
  - You can implement this interface to decorate the message being logged. (Simply implement it, LoggEE will automatically
      use it instead of the default one.)
3. Define own ```loggee.api.LoggerProducer```.
  - With this you can tell LoggEE how to produce ```org.slf4j.Logger``` instances. (Again, simply implement it, LoggEE will automatically 
      use it instead of the default one.)

Decision Bean Pattern
---------------------

Logging at the start and end of method calls are fine but what about the inside of the methods? Although LoggEE really
can't do much about it by itself, using the Decision Bean Pattern can help you leverage LoggEE's capabilities in logging
important decisions _inside_ your methods.

How? It's a good practice to wrap any composite expression inside an _if_ statement and extract it into a local method.
Simply take another step forward and extract such expressions (no matter how simple they are) into another bean.
We can call this bean a Decision Bean. This Decision Bean can be annotated with _@Logged_, injected into your original
class, and now LoggEE will be able to log these decisions taking place inside your method!

Here's an example:

A service class:
```java
package my.pack;

import javax.inject.Inject;

import loggee.api.Logged;
import my.pack.decision.MyDecisionService;

@Logged
public class MyService {
    @Inject
    private MyDecisionService myDecisionService;

    public void doSomething(Object input) {
        if (myDecisionService.firstDecisionBasedOnInput(input)) {
            // Do something
        } else if (myDecisionService.secondDecisionBasedOnInput(input)) {
            // Do something else
        }
    }
}
```

A __decision bean__ for the previous class:
```java
package my.pack.decision;

import loggee.api.Logged;

@Logged
public class MyDecisionService {

    public boolean firstDecisionBasedOnInput(Object input) {
        // Make the decision
        return false;
    }

    public boolean secondDecisionBasedOnInput(Object input) {
        // Make the decision
        return true;
    }

}
```

Log output:

```
12:00:00,001 DEBUG [METHOD_CALL.my.pack.MyService] (http-localhost-127.0.0.1-8080-1) STARTED MyService.doSomething
12:00:00,001 DEBUG [PARAMETER.METHOD_CALL.my.pack.MyService] (http-localhost-127.0.0.1-8080-1) MyService.doSomething 0 : java.lang.Object@1b4caafa
12:00:00,001 DEBUG [DECISION.my.pack.decision.MyDecisionService] (http-localhost-127.0.0.1-8080-1) MyDecisionService.firstDecisionBasedOnInput false
12:00:00,001 DEBUG [PARAMETER.DECISION.my.pack.decision.MyDecisionService] (http-localhost-127.0.0.1-8080-1) MyDecisionService.firstDecisionBasedOnInput 0 : java.lang.Object@1b4caafa
12:00:00,001 DEBUG [DECISION.my.pack.decision.MyDecisionService] (http-localhost-127.0.0.1-8080-1) MyDecisionService.secondDecisionBasedOnInput true
12:00:00,001 DEBUG [PARAMETER.DECISION.my.pack.decision.MyDecisionService] (http-localhost-127.0.0.1-8080-1) MyDecisionService.secondDecisionBasedOnInput 0 : java.lang.Object@1b4caafa
12:00:00,001 DEBUG [METHOD_CALL.my.pack.MyService] (http-localhost-127.0.0.1-8080-1) FINISHED MyService.doSomething in 3ms : NULL
```

