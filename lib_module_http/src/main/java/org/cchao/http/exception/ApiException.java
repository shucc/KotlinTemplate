package org.cchao.http.exception;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class ApiException extends Exception {

    public ApiException(int code, String message) {
        super(message);
    }
}
