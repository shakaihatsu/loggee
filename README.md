Loggee
======

Automatic Logging Solution For Java EE Applications

Overview
--------

Loggee is a simple yet powerful, light-weight, easy-to-use, highly customizable automatic logging module that can take
care of the very important yet troublesome task of logging in your Java EE application.
Usint AOP techniques, with a well designed codebase, it is capable of logging almost anything and everything your
application may need to be logged.

Using CDI itself, the current implementation of Loggee supports only other applications that also use CDI.
Loggee uses slf4j as backing logging framework.

How to install
--------------

Loggee is a Maven project. Once it has been cloned, it can be installed into any Maven repository and then added
as a dependency into any other Maven project.

How to use
----------

1. Add Loggee as a Maven dependency
2. Add ```loggee.api.LoggeeInterceptor``` to your ```beans.xml``` as an interceptor, like this:
```xml
<?xml version="1.0"?>
<beans xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd">
  
      <interceptors>
        <class>loggee.api.LoggeeInterceptor</class>
      </interceptors>
</beans>
```
3. Annotate your class or your method with ```loggee.api.Logged```, like this:
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

Loggee actually logs method calls, handling boolean (referenced as _"decision"_) and other (referenced as _"regular"_)
methods differently:
  - Regular methods
    1. __Start__ of the method
    2. __Parameters__
    3. __End__ of the method, the __return value__ and the __duration__
    4. __Parameters__ again (they may have changed in the method) - This is optional and OFF by default (see Configuration)
  - Decision methods
    1. __Return value__
    2. __Parameters__
    
This means that by default, decision methods are logged only after they are called. This behaviour can be changed,
telling Loggee to consider a boolean method (or all the boolean methods of class, depending where you put the _@Logged_
annotation) as "regular" (see Configuration).

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
depending what is being logged. This is to help configure logging for these kind of logs universally.

Additional features
-------------------

Loggee also provides ```org.slf4j.Logger``` loggers, so you can simply inject them into your managed beans like this:

```java
import javax.inject.Inject;
import org.slf4j.Logger;

public class MyClass {
    @Inject
    private Logger logger;
```

(Please keep in mind that this may be a restriction as well, in case you want to make your own producer of
```org.slf4j.Logger``` instances. For this reason, Loggee can be configured how to produce ```org.slf4j.Logger```.
See Configuration.)

Configuration
-------------

There are a couple of ways to configure Loggee:

1. Configure the ```loggee.api.Logged``` annotation
  - logLevel - Log level for regular methods - Default: DEBUG
  - parameterLogLevel - Log level for parameters - Default: DEBUG
  - failureLogLevel - Log level for when the method throws an exception - Default: WARN
  - regularMethodLoggerBaseName - Logger base name for regular methods - Default: "METHOD_CALL"
  - decisionMethodLoggerBaseName - Logger base name for decision methods - Default: "DECISION"
  - parameterLoggerBaseName - Logger base name for parameters of regular methods - Default: "PARAMETER"
  - decisionParameterLoggerBaseName - Logger base name for parameters of decision methods - Default: "PARAMETER"
  - booleanMethodLogPolicy - How to treat boolean methods - Default: DECISION
2. Define own ```loggee.api.LogLineFormatter```.
  You can implement this interface to decorate the message being logged. (Simply implement it, Loggee will automatically
  use this instead of the default one.)
3. Define own ```loggee.api.LoggerProducer```.
  With this you can tell Loggee how to produce ```org.slf4j.Logger``` instances. (Again, simply implement it, Loggee will automatically
  use this instead of the default one.)

