package org.example.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author hanyangyang
 * @since 2025/8/16
 **/
public class ReflectLearn {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        Class<?> aClass = Class.forName("org.example.reflect.ReflectBaseClass");
        // 需要确保有无参构造方法
        Object o = aClass.newInstance();
        /*
        getFields()：获得某个类的所有的公共（public）的字段，包括父类中的字段。
        getDeclaredFields()：获得某个类的所有声明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
        同样类似的还有getConstructors()和getDeclaredConstructors()、getMethods()和getDeclaredMethods()，这两者分别表示获取某个类的方法、构造函数。
         */
        Field privateField = aClass.getDeclaredField("privateField");
        /*
        用于设置反射对象的可访问性标志。通过设置为 true，可以在运行时绕过 Java 语言的访问控制检查，从而访问类的私有成员变量或方法。
        */
        privateField.setAccessible(true);
        privateField.set(o,"反射类的privateField属性");
        System.out.println(o.toString());


        Method setPrivateFieldMethod = aClass.getMethod("setPrivateField", String.class);
        setPrivateFieldMethod.invoke(o,"通过方法修改属性");
        System.out.println(o.toString());
    }
}
