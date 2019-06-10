# APT





2.新建一个java的module，命名为apt，在build.gradle中配置
```
apply plugin: 'java-library'

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    compile 'com.google.auto.service:auto-service:1.0-rc2' //谷歌提供的注解自动化生成
    compile 'com.squareup:javapoet:1.9.0' //这个包主要用来简化创建文件的代码

    compile project(':annotation') //这个包内包含的是注解

}

tasks.withType(JavaCompile) {
    options.encoding = "UTF-8"
}

sourceCompatibility = "1.8"
targetCompatibility = "1.8"
```
