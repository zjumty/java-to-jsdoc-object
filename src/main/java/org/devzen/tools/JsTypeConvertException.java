package org.devzen.tools;

/**
 * User: matianyi
 * Date: 14-4-20
 * Time: 下午10:54
 */
public class JsTypeConvertException extends RuntimeException {
    public JsTypeConvertException() {
    }

    public JsTypeConvertException(String s) {
        super(s);
    }

    public JsTypeConvertException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public JsTypeConvertException(Throwable throwable) {
        super(throwable);
    }
}
