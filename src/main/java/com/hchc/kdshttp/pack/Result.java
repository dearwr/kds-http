package com.hchc.kdshttp.pack;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@Data
@NoArgsConstructor
public class Result {

    private int code;
    private String message;
    private Object data;

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result ok() {
        return ok("success");
    }

    public static Result ok(String msg) {
        return ok(msg, null);
    }

    public static Result ok(Object data) {
        return ok("success", data);
    }

    public static Result ok(String msg, Object data) {
        return new Result(0, msg, data);
    }

    public static Result fail() {
        return fail("fail");
    }

    public static Result fail(String msg) {
        return fail(msg, null);
    }

    public static Result fail(String msg, Object data) {
        return new Result(999, msg, data);
    }

    public static Result fail(Exception e) {
        return fail("server happen error:" + e.getMessage());
    }

}
