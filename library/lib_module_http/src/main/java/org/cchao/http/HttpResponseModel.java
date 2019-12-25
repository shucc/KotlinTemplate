package org.cchao.http;

import androidx.annotation.Keep;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
@Keep
public class HttpResponseModel<T> {

    public static final int CODE_ERROR = -1;
    public static final int CODE_NOT_NETWORK = 404;

    private int code;

    private String msg;

    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}
