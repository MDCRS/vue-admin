package com.example.common.exception;

/**
 * Company :  北京动力节点
 * Author :   Andy
 * Date : 2021/7/19
 * Description :
 */
public class SysException extends RuntimeException {

    private SysEnum sysEnum;

    public SysException(SysEnum sysEnum) {
        //想从异常堆栈中获取异常信息的话
        super(sysEnum.getMessage());
        this.sysEnum = sysEnum;
    }
}
