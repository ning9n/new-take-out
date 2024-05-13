package common.result;

import lombok.Data;

/**
 * 请求结果封装
 * @param <T>
 */
@Data
public class SkyResult<T> {
    /*
    code:
    0：请求失败
    1：请求成功
     */
    int code;
    String message;//错误信息
    T data;//返回数据
    public static  SkyResult<String> success(){
        SkyResult<String> skyResult=new SkyResult<>();
        skyResult.setCode(1);
        return skyResult;
    }
    public static <T> SkyResult<T> success(T data){
        SkyResult<T> skyResult=new SkyResult<>();
        skyResult.setCode(1);
        skyResult.setData(data);
        return skyResult;
    }
    public static <T> SkyResult<T> error(String msg){
        SkyResult<T> skyResult=new SkyResult<>();
        skyResult.setCode(0);
        skyResult.setMessage(msg);
        return skyResult;
    }
}
