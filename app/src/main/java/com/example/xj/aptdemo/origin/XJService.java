package com.example.xj.aptdemo.origin;

import com.example.annolib.anno.InterfaceCreateAnno;

/**
 * 这是APT的原始类，加上注解之后，在generate生成对应类
 * Created by xj on 2019/6/7.
 */



@InterfaceCreateAnno
public class XJService {

    private String name;
    private int age;
    public boolean sex;
    public String desc;

    public int mathCaculate() {

        int a = 10;
        int b = 15;

        return a + b;
    }

    private String addDesc() {

        String front = "xj is cool";
        String end = "yes i do";

        return front + end;
    }
}
