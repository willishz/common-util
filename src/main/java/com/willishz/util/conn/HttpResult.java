package com.willishz.util.conn;

/**
 * User: liuchuandong
 * Date: 13-9-5
 * Time: 下午3:31
 * Func:
 */
public class HttpResult {

    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
