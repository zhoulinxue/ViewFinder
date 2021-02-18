package org.zhx.common.apt.annotation;

public class TargetInfo {
    private Object target;
    private Object View;

    public TargetInfo(Object target, Object view) {
        this.target = target;
        View = view;
    }

    public Object getTarget() {
        return target;
    }

    public Object getView() {
        return View;
    }
}
