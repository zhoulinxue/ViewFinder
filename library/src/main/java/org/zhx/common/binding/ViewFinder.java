package org.zhx.common.binding;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;

import org.zhx.common.apt.annotation.Constants;
import org.zhx.common.apt.annotation.IViewFinder;
import org.zhx.common.apt.annotation.TargetInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ViewFinder {
    private static Map<String, IViewFinder> map = new ConcurrentHashMap<>();

    public static void bind(Activity target) {
        bind(target, target.getWindow().getDecorView());
    }

    public static void bind(Dialog target) {
        bind(target, target.getWindow().getDecorView());
    }

    public static void bind(View target) {
        bind(target, target);
    }

    public static void bind(Object target, View view) {
        if (view == null) {
            throw new NullPointerException("ViewFinder  view==null");
        }
        String className = target.getClass().getCanonicalName();
        IViewFinder helper = map.get(className);
        if (helper == null) {
            String helperName = className + Constants.VIEW_SUFIX;
            try {
                helper = (IViewFinder) (Class.forName(helperName).getConstructor().newInstance());
                map.put(className, helper);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        helper.bind(new TargetInfo(target, view));
    }
}
