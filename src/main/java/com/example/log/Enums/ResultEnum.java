package com.example.log.Enums;

public enum ResultEnum {
    SUCCESS(10001,"请求成功"),
    SYSTEM_ERROR(10002,"未知异常，请联系管理员"),
    CUSTOM_ERROR(99999,"自定义异常"),
    VALID_ERROR(10003,"参数校验异常"),
    JSON_PARSE_ERROR(10004,"JSON转换异常");

    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

