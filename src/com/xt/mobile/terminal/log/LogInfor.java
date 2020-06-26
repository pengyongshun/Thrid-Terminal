package com.xt.mobile.terminal.log;

//日志信息
public class LogInfor {
    //标签tag
    private String tag;
    //错误码
    private String errorNumber;
    //类的名称
    private String className;
    //方法的名称
    private String methodName;
    //消息描述
    private String msgInfo;
    
    public LogInfor(){
        
    }
    
    
    public LogInfor(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        this.tag = tag;
        this.errorNumber = errorNumber;
        this.className = className;
        this.methodName = methodName;
        this.msgInfo = msgInfo;
    }

    public String getErrorNumber() {
        return errorNumber;
    }

    public void setErrorNumber(String errorNumber) {
        this.errorNumber = errorNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "LogInfor [errorNumber=" + errorNumber + ", className="
                + className + ", methodName=" + methodName + ", msgInfo="
                + msgInfo + "]";
    }

}
