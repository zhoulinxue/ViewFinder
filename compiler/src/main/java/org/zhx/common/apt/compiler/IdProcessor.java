package org.zhx.common.apt.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.zhx.common.apt.annotation.Constants;
import org.zhx.common.apt.annotation.FindView;
import org.zhx.common.apt.annotation.ViewInfo;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

@AutoService(Processor.class)
public class IdProcessor extends BaseProcessor {
    private String TAG = IdProcessor.class.getSimpleName();
    private Map<TypeElement, Set<ViewInfo>> mToBindMap = new HashMap<>();

    @Override
    protected TypeSpec customProcess(TypeElement enclosingElement, String parent, ClassName targetClass) {
        ClassName viewClazz = ClassName.get("android.view", "View");
        ClassName iViewFinder = ClassName.get("org.zhx.common.apt.annotation", "IViewFinder");
        ClassName targetFinder = ClassName.get("org.zhx.common.apt.annotation", "TargetInfo");
        //初始化类
        String className = targetClass.simpleName() + Constants.VIEW_SUFIX;
        // 方法
        MethodSpec.Builder bind = MethodSpec.methodBuilder("bind")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(targetFinder, "targetInfo")
                .addStatement("bind((($T)targetInfo.getTarget()),(($T)targetInfo.getView()))", targetClass, viewClazz);

        MethodSpec.Builder viewM = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(targetClass, "target")
                .addParameter(viewClazz, "view");


        for (ViewInfo info : mToBindMap.get(enclosingElement)) {
            viewM.addStatement("target.$N=$N.findViewById($L)", info.viewName, "view", info.id);
            if (info.valus != null && info.valus.length() > 0) {
                viewM.addStatement("target.$N.setText($S)", info.viewName, info.valus);
            }
        }
        // 生成的类
        TypeSpec.Builder helperClazz = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(iViewFinder)
                .addMethod(viewM.build())
                .addMethod(bind.build());
        return helperClazz.build();
    }


    @Override
    protected Class<? extends Annotation> initElementClass() {
        return FindView.class;
    }

    @Override
    protected void preprocess(Set<? extends Element> elements) {
        for (Element ele : elements) {
            VariableElement variableElement = (VariableElement) ele;
            TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();
            categories(variableElement, enclosingElement);
        }
    }

    protected void categories(VariableElement variableElement, TypeElement enclosingElement) {
        Set<ViewInfo> views = mToBindMap.get(enclosingElement);
        if (views == null) {
            views = new HashSet<>();
            mToBindMap.put(enclosingElement, views);
        }
        FindView bindAnnotation = (FindView) variableElement.getAnnotation(initElementClass());
        int id = bindAnnotation.id();
        String name = bindAnnotation.content();
        views.add(new ViewInfo(variableElement.getSimpleName().toString(), id, name));
    }

}
