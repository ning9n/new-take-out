package common.exception;

/**
 * 用户在数据库未找到
 */
public class UserNotFoundException extends BaseException{
    public UserNotFoundException(){

    }
    public UserNotFoundException(String msg){
        message=msg;
    }
}
