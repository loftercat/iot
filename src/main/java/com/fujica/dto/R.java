package com.fujica.dto;

import com.fujica.constant.HeaderConstant;
import com.fujica.exception.ExceptionCode;
import com.fujica.exception.ServiceException;
import lombok.Data;
import org.slf4j.MDC;

@Data
public class R<T> {
    private int code;
    private String msg;
    private T data;
    private Boolean success = true;
    private String reqId;


    public R() {
        reqId = MDC.get(HeaderConstant.TRACE_ID);
    }


    public static <T> R<T> ok(T data)
    {
        R<T> r = new R<>();
        r.setCode(0);
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok()
    {
        R<T> r = new R<>();
        r.setCode(0);
        return r;
    }

    public static <T> R<T> error(String msg)
    {
        R<T> r = new R<>();
        r.setCode(1);
        r.setMsg(msg);
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> error(int code, String msg)
    {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> error(ExceptionCode exceptionCode) {
        R<T> r = new R<>();
        r.setCode(exceptionCode.getCode());
        r.setMsg(exceptionCode.getMsg());
        r.setSuccess(false);
        return r;
    }


    public static <T> R<T> error(ExceptionCode exceptionCode, String msg) {
        R<T> r = new R<>();
        r.setCode(exceptionCode.getCode());
        r.setMsg(msg);
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> error(ServiceException exception) {
        R<T> r = new R<>();
        r.setCode(exception.getCode());
        r.setMsg(exception.getMessage());
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> error(ServiceException exception, String msg) {
        R<T> r = new R<>();
        r.setCode(exception.getCode());
        r.setMsg(msg);
        r.setSuccess(false);
        return r;
    }
}
