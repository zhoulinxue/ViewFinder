package org.zhx.common.apt.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public abstract class BaseProcessor extends AbstractProcessor {
    private Elements mElementsUtils;
    private Class<? extends Annotation> mClass;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementsUtils = processingEnv.getElementUtils();
        mClass = initElementClass();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(mClass.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (null == set || set.isEmpty()) {
            return false;
        } else {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(mClass);
            preprocess(elements);
            for (Element ele : elements) {
                VariableElement variableElement = (VariableElement) ele;
                TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();
                //parent class
                String parent = enclosingElement.getSuperclass().toString();
                //class path
                String packageName = (mElementsUtils.getPackageOf(variableElement)).getQualifiedName().toString();
                // class name
                String rawClassName = enclosingElement.getSimpleName().toString();
                // holl class path
                ClassName targetClass = ClassName.get(packageName, rawClassName);
                TypeSpec helperClazz = customProcess(enclosingElement, parent, targetClass);
                JavaFile javaFile = JavaFile.builder(packageName, helperClazz)
                        .build();
                try {
                    javaFile.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }

    protected abstract void preprocess(Set<? extends Element> elements);

    protected abstract void categories(VariableElement variableElement, TypeElement enclosingElement);

    protected abstract TypeSpec customProcess(TypeElement enclosingElement, String parent, ClassName targetClass);

    protected abstract Class<? extends Annotation> initElementClass();
}
