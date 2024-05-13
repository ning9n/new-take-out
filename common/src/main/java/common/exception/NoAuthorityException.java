package common.exception;

/**
 * 没有相应权限
 */
public class NoAuthorityException extends BaseException{
    public NoAuthorityException(){
        message="没有相应权限";
    }
    public NoAuthorityException(String msg){
        message=msg;
    }
}
