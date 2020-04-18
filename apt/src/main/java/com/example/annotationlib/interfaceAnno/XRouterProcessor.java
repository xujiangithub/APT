package com.example.annotationlib.interfaceAnno;


import com.example.annolib.anno.InterfaceCreateAnno;
import com.example.annolib.anno.XRouter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;


/**
 * 这个注解是必须的，不加的话在build的时候会找不到该注解处理器
 */
@AutoService(Processor.class)
public class XRouterProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    /**
     * 该方法用于指定该注解处理器出路哪些注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(XRouter.class.getCanonicalName());
    }

    /**
     * 用于指定java版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Boolean shouldGenerateFile = false; // 是否生成文件
        String targetClassName = ""; // 生成文件名
        String packageName = ""; // 包名

        List<XRouteBean> routeList = new ArrayList(); // 映射关系存储列表

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(XRouter.class);

        for (Element element : elements) {
            if (element instanceof TypeElement) {
                if (element instanceof TypeElement) {
                    shouldGenerateFile = true;
                    if (targetClassName.isEmpty()) {
                        targetClassName = "XRouter$$" + element.getSimpleName(); // 生成文件名
                        packageName = getPackageName((TypeElement) element);
                    }
                    // 将映射关系放入列表中
                    routeList.add(new XRouteBean(element.getAnnotation(XRouter.class).path(), element.asType().toString()));
                }
            }
        }

        if (shouldGenerateFile) {
            MethodSpec.Builder methodSpec = getMethodSpec("loadInto");

            // 循环读取映射关系，放入方法体内
            for (XRouteBean item: routeList) {
                methodSpec.addStatement("xRouteMap.put($S, $S)", item.path, item.routePath);
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(targetClassName) //声明类名,interfaceBuilder生成接口，classBuilder生成类
                    .addModifiers(Modifier.PUBLIC) //声明类的修饰符，可以，隔开
                    .addSuperinterface(XRouterInterface.class)
                    .addMethod(methodSpec.build())
                    .build();

            JavaFile javaFile = null;
            javaFile = JavaFile
                    .builder(packageName, typeSpec)
                    .addFileComment("XRouter生成的path-route注入类") //注释
                    .addFileComment("\ncreate by xujian apt") //注释
                    .build();


            try {
                javaFile.writeTo(filer); //写出
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private MethodSpec.Builder getMethodSpec(String loadIntoJsonFile) {
        return MethodSpec.methodBuilder(loadIntoJsonFile)
                .addModifiers(Modifier.PUBLIC)//指定方法修饰符为 public
                .addAnnotation(Override.class)
                .addParameter(getParameterSpec());
    }

    private ParameterSpec getParameterSpec() {
        return ParameterSpec.builder(Map.class, "xRouteMap").build();
    }

    /**
     * 获取包名
     *
     * @param typeElement
     * @return
     */
    public String getPackageName(TypeElement typeElement) {
        return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
    }
}

/**
 * 路由映射bean
 */
class XRouteBean {
    String path;
    String routePath;

    XRouteBean(String path, String routePath) {
        this.path = path;
        this.routePath = routePath;
    }
}