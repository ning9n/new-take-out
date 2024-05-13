package common.exception;

/**
 * 非法数据
 */
public class LllegalDataException extends BaseException{
    public LllegalDataException(){
        message="非法数据";
    }
    public LllegalDataException(String msg){
        message=msg;
    }
}
