package org.example.demo777.common;


public class Result<T> {
    private int code;
    private String msg;
    private T data;
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.data = data;
        result.code = 200;
        result.msg = "success";
        return result;
    }
    public static <T> Result<T>error(int code, String msg) {
        Result<T> result = new Result<T>();
        result.code = code;
        result.msg = msg;
        return result;
    }
}
