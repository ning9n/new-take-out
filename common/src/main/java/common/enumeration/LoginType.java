package common.enumeration;

public class LoginType {
    //商家，具有所有权限，
    public static final String MERCHANT="merchant";
    //管理员，能修改商店信息
    public static final String ADMIN="admin";
    //能修改员工信息
    public static final String SUPER_ADMIN="superAdmin";
    //普通员工，只有基本权限
    public static final String EMPLOYEE="employee";
}
