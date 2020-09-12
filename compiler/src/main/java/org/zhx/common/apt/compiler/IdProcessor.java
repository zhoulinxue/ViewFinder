package org.zhx.common.apt.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.zhx.common.apt.annotation.Constants;
import org.zhx.common.apt.annotation.FindView;
import org.zhx.common.apt.annotation.ViewInfo;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class IdProcessor extends BaseProcessor {
    private Filer mFilerUtils;
    private Types mTypesUtils;
    private Elements mElementsUtils;
    private Map<TypeElement, Set<ViewInfo>> mToBindMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFilerUtils = processingEnv.getFiler();
        mTypesUtils = processingEnv.getTypeUtils();
        mElementsUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (null == set || set.isEmpty()) return false;
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FindView.class);
        categories(elements);
        for (Element ele : elements) {
            VariableElement variableElement = (VariableElement) ele;
            TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();

            String packageName = ((PackageElement) mElementsUtils.getPackageOf(variableElement)).getQualifiedName().toString();

            String rawClassName = enclosingElement.getSimpleName().toString();

            String className = rawClassName + Constants.VIEW_SUFIX;

            ClassName targetClass = ClassName.get(packageName, rawClassName);

            ClassName iViewFinder = ClassName.get("org.zhx.common.apt.annotation", "IViewFinder");

            ClassName textView = ClassName.get("android.widget", "TextView");

            ClassName fragment = ClassName.get("androidx.fragment.app", "Fragment");

            ClassName activity = ClassName.get("android.app", "Activity");
            String parent = enclosingElement.getSuperclass().toString();
            boolean isActivity = parent.contains("Activity") && parent.startsWith("android");
            MethodSpec.Builder bind = MethodSpec.methodBuilder("bind")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(targetClass, "target");
            for (ViewInfo info : mToBindMap.get(enclosingElement)) {
                bind.addStatement("target.$N=$N.findViewById($L)", info.viewName, isActivity ? "target" : "target.getView()", info.id);
            }
            TypeSpec.Builder helperClazz = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ParameterizedTypeName.get(iViewFinder, targetClass))
                    .addMethod(bind.build());

            JavaFile javaFile = JavaFile.builder(packageName, helperClazz.build())
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(FindView.class.getCanonicalName());
    }

    private String generateCode(TypeElement typeElement) {
        String rawClassName = typeElement.getSimpleName().toString();
        String packageName =
                ((PackageElement) mElementsUtils.getPackageOf(typeElement)).getQualifiedName()
                        .toString();
        String helperClassName = rawClassName + Constants.VIEW_SUFIX;

        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(packageName).append(";\n");
        builder.append("import org.zhx.common.apt.annotation.IViewFinder;\n\n");
        builder.append("import android.widget.TextView;\n\n");

        builder.append("public class ")
                .append(helperClassName)
                .append(" implements ")
                .append("IViewFinder");
        builder.append(" {\n");
        builder.append("\t@Override\n");
        builder.append("\tpublic void bind(" + "Object" + " target ) {\n");
        for (ViewInfo viewInfo : mToBindMap.get(typeElement)) {
            builder.append("\t\t");
            builder.append(
                    rawClassName + " substitute = " + "(" + rawClassName + ")" + "target;\n");

            builder.append("\t\t");
            builder.append("substitute." + viewInfo.viewName).append(" = ");
            builder.append("substitute.findViewById(" + viewInfo.id + ");\n");
            builder.append("\t\t");
            builder.append("if(substitute." + viewInfo.viewName + " instanceof " + "TextView){\n");
            builder.append("\t\t\t");
            builder.append("substitute." + viewInfo.viewName + ".setText(" + "\"" + viewInfo.valus + "\"" + ");\n");

            builder.append("\t\t");
            builder.append("}\n");

        }
        builder.append("\t}\n");
        builder.append('\n');
        builder.append("}\n");

        return builder.toString();
    }

    private void categories(Set<? extends Element> elements) {
        for (Element element : elements) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement enclosingElement =
                    (TypeElement) variableElement.getEnclosingElement();
            Set<ViewInfo> views = mToBindMap.get(enclosingElement);
            if (views == null) {
                views = new HashSet<>();
                mToBindMap.put(enclosingElement, views);
            }
            FindView bindAnnotation = variableElement.getAnnotation(FindView.class);
            int id = bindAnnotation.id();
            views.add(new ViewInfo(variableElement.getSimpleName().toString(), id, ""));
        }
    }

}
