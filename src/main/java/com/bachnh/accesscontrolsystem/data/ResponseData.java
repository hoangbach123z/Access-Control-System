package com.bachnh.accesscontrolsystem.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ResponseData<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String timestamp;


    private int code;

    private String message;

    private T data;

    public ResponseData() {
        this.code = 0;
        this.timestamp =
                LocalDateTime.now()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.message = "Successful!";
    }

    ResponseData<T> success(T data) {
        this.data = data;
        return this;
    }

    ResponseData<T> success(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
        return this;
    }

    ResponseData<T> error(int code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    ResponseData<T> error(int code, String message, T data) {
        this.data = data;
        this.code = code;
        this.message = message;
        return this;
    }

}