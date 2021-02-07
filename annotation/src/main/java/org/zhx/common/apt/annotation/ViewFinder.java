package org.zhx.common.apt.annotation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ViewFinder {
    private static Map<String, IViewFinder> map = new ConcurrentHashMap<>();

    public static void init(Object target) {
        String className = target.getClass().getCanonicalName();
        IViewFinder helper = map.get(className);
        if (helper == null) {
            String helperName = className + Constants.VIEW_SUFIX;
            System.out.println("!!!!!!!!!!"+helperName);
            try {
                helper = (IViewFinder) (Class.forName(helperName).getConstructor().newInstance());
                map.put(className, helper);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        helper.bind(target);
    }
}
