package com.chenyacheng;

/**
 * @author chenyacheng
 * @date 2019/12/16
 */
public interface ResponseListener {

    void success(Object data);

    void failure(String error);
}
