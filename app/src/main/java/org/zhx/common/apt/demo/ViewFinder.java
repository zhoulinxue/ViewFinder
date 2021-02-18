package org.zhx.common.apt.demo;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.zhx.common.apt.annotation.Constants;
import org.zhx.common.apt.annotation.IViewFinder;
import org.zhx.common.apt.annotation.TargetInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ViewFinder {
    private static Map<String, IViewFinder> map = new ConcurrentHashMap<>();

    public static void init(Activity target) {
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
        helper.bind(new TargetInfo(target, target.getWindow().getDecorView()));
    }

    public static void init(Fragment target, View view) {
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
