package com.testjaxrs.dto;

public enum ResponseMessageEnum {
    OK("200","ok");

  String code;
  String msg;

   ResponseMessageEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
