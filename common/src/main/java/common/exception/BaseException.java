package common.exception;

/**
 * 业务异常
 */
public class BaseException extends RuntimeException {

    String message;
    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
