package common.result;

import java.util.List;

public class  PageResult<T> {
    //总条数
    Long total;
    //数据
    List<T> records;
    public PageResult(Long total,List<T> records){
        this.total=total;
        this.records=records;
    }
 }
