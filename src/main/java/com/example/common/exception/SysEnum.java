package com.example.common.exception;

/**
 * Company :  北京动力节点
 * Author :   Andy
 * Date : 2021/7/19
 * Description :
 */
public enum SysEnum {

    SMS("001", "给车主的短信发送失败！"),
    BALANCE("002", "车主账号余额不足，请联系该车主，必要时请报警！");


    private String typeCode;//属于哪个模块下的操作失败code
    private String message;//具体错误消息

    SysEnum(String typeCode, String message) {
        this.typeCode = typeCode;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
