package com.example.annotationlib.interfaceAnno;

import com.example.annolib.anno.InterfaceCreateAnno;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
 * Created by xj on 2019/6/7.
 */

/**
 * 这个注解是必须的，不加的话在build的时候会找不到该注解处理器
 */
@AutoService(Processor.class)
public class InterfaceCreateProcessor extends AbstractProcessor {

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
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(InterfaceCreateAnno.class.getCanonicalName());
    }

    /**
     * 用于指定java版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(InterfaceCreateAnno.class);

        for (Element element : elements) {
            System.out.print(element.asType());
            if (element instanceof TypeElement) {
                String targetClassName = "I" + element.getSimpleName();

                List<MethodSpec> methodSpecs = new ArrayList<>();

                List<TypeSpec> typeSpecs = Collections.emptyList();
                TypeSpec typeSpec = TypeSpec.interfaceBuilder(targetClassName) //声明类名,interfaceBuilder生成接口，classBuilder生成类
                        .addModifiers(Modifier.PUBLIC) //声明类的修饰符，可以，隔开
                        .addMethods(methodSpecs)
                        .addMethod(getMethodSpec("myCreateMethod", "String"))//为targetClassName类添加名为myCreateMethod的方法，返回值为String
                        .addTypes(typeSpecs)
                        .build();

                JavaFile javaFile = JavaFile
                        .builder(getPackageName((TypeElement) element), typeSpec)
                        .addFileComment("InterfaceCreateProcessor生成的接口") //注释
                        .addFileComment("\ncreate by xujian apt") //注释
                        .build();

                try {
                    javaFile.writeTo(filer); //写出
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 为类添加方法
     * @param methodStr  方法名
     * @param returnStr  返回值
     * @return
     */
    private static MethodSpec getMethodSpec(String methodStr, String returnStr) {
        return MethodSpec.methodBuilder(methodStr)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)//指定方法修饰符为 public static
                .returns(String.class) //指定返回值为String类型
//                .addStatement("return $S", returnStr) //拼接返回值语句
                .build();
    }

    /**
     * 获取包名
     * @param typeElement
     * @return
     */
    public String getPackageName(TypeElement typeElement) {
        return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
    }
}
