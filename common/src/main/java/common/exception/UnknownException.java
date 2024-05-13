package common.exception;

public class UnknownException extends BaseException{
    public UnknownException(){
        message="未知错误";
    }
    public UnknownException(String msg){
        message=msg;
    }
}
