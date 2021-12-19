package org.zhx.common.apt.annotation;

/**
 * @ProjectName: demo
 * @Package: org.zhx.common.apt.compiler
 * @ClassName: ViewInfo
 * @Description:java
 * @Author: zhouxue
 * @CreateDate: 2020/9/3 10:27
 * @UpdateUser:
 * @UpdateDate: 2020/9/3 10:27
 * @UpdateRemark:
 * @Version:1.0
 */
public class ViewInfo {
    public String viewName;
    public int id;
    public String valus;
    public int src;

    public ViewInfo(String viewName, int id, String valus, int src) {
        this.viewName = viewName;
        this.id = id;
        this.valus = valus;
        this.src = src;
    }
}
