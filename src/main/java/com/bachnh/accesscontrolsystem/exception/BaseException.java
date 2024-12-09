//package com.bachnh.accesscontrolsystem.exception;
//
//import lombok.Getter;
//import org.springframework.http.HttpStatus;
//
//@Getter
//public class BaseException extends RuntimeException {
//
//    private final Object[] messageArg;
//
//    private final AbstractError abstractError;
//
//    private final Throwable cause;
//
//    public BaseException(AbstractError abstractError) {
//        this(null, abstractError, null);
//    }
//
//    public BaseException(AbstractError abstractError, Throwable cause) {
//        this(null, abstractError, cause);
//    }
//
//    public BaseException(Object[] messageArg, AbstractError abstractError, Throwable cause) {
//        super(abstractError.getMessage(), cause);
//        this.messageArg = messageArg;
//        this.abstractError = abstractError;
//        this.cause = cause;
//    }
//
//    public int getCode() {
//        return abstractError.getCode();
//    }
//
//    public String getMessage() {
//        return messageArg == null
//                ? abstractError.getMessage()
//                : String.format(abstractError.getMessage(), messageArg);
//    }
//
//    public String getLocalizedMessage() {
//        return BaseException.class.getSimpleName()
//                + "["
//                + abstractError.getCode()
//                + "-"
//                + getMessage()
//                + "-"
//                + abstractError.getHttpStatus()
//                + "]";
//    }
//
//    public HttpStatus getHttpStatus() {
//        return abstractError.getHttpStatus();
//    }
//}