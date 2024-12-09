package com.fujica.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final int code;


    public ServiceException(ExceptionCode code) {
        super(code.getMsg());
        this.code = code.getCode();
    }

    public ServiceException(ExceptionCode code, String msg) {
        super(msg);
        this.code = code.getCode();
    }



}
