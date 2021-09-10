package com.yht.common.api;

/**
 * 封装API的错误码
 *
 * @author 鱼仔
 * @date 2019/4/19
 */
public interface IErrorCode {
    long getCode();

    String getMessage();
}
