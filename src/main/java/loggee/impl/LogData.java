package loggee.impl;

public class LogData {
    private String targetClassName;
    private String targetClassSimpleName;
    private String methodName;
    private String methodInvocationAsString;

    public final String getTargetClassName() {
        return targetClassName;
    }

    public final void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public final String getTargetClassSimpleName() {
        return targetClassSimpleName;
    }

    public final void setTargetClassSimpleName(String targetClassSimleName) {
        this.targetClassSimpleName = targetClassSimleName;
    }

    public final String getMethodName() {
        return methodName;
    }

    public final void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public final String getMethodInvocationAsString() {
        return methodInvocationAsString;
    }

    public final void setMethodInvocationAsString(String methodInvocationAsString) {
        this.methodInvocationAsString = methodInvocationAsString;
    }

    @Override
    public String toString() {
        return "LogData [targetClassName=" + targetClassName + ", targetClassSimpleName=" + targetClassSimpleName + ", methodName=" + methodName
                + ", methodInvocationAsString=" + methodInvocationAsString + "]";
    }

}
