# 简易版APT，完成了这个简易版生成之后，后续有一些难度的APT，就是在这个的基础上增加一些逻辑即可。

## 1.新建一个Java Library 命名为annotation，在build.gradle中配置
```

apply plugin: 'java-library'

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}

sourceCompatibility = "1.8"
targetCompatibility = "1.8"

//这个是为了防止gbk编码乱码问题
tasks.withType(JavaCompile) {
    options.encoding = "UTF-8"
}

```
## 2.创建一个注解
```
@Retention(RetentionPolicy.RUNTIME)  //RetentionPolicy.RUNTIME表示运行时注解
@Target(ElementType.TYPE) //表明该注解是作用于类上面
public @interface InterfaceCreateAnno {
}
```


## 3.新建一个Java Library，命名为apt，在build.gradle中配置
```
apply plugin: 'java-library'

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    compile 'com.google.auto.service:auto-service:1.0-rc2' //谷歌提供的注解自动化生成
    compile 'com.squareup:javapoet:1.9.0' //这个包主要用来简化创建文件的代码

    compile project(':annotation') //这个包内包含的是注解。

}

//这个是为了防止gbk编码乱码问题
tasks.withType(JavaCompile) {
    options.encoding = "UTF-8"
}

sourceCompatibility = "1.8"
targetCompatibility = "1.8"

```

## 4.定义注解处理类，继承自AbstractProcessor,这里给出一个简单的例子，注释都详尽写在代码后面了
```
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
//                .addStatement("return $S", returnStr) //拼接返回值语句,生成的如果是接口，方法应该是抽象方法，且没有实现
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

```

## 5.在app模块的build.gradle中配置如下
```
    compile project(':annotation')
    annotationProcessor project(':apt')
```


## 6.在app模块下新建一个类，使用上面自定义的注解进行注解
```
@InterfaceCreateAnno()
public class XJService {
}
```

## 7.执行rebuild，你就会发现在app/build/source/apt/debug下面，就已经生成了需要的类
```
package com.example.xj.aptdemo.origin;

import java.lang.String;

public interface IXJService {
  String myCreateMethod();
}

```


