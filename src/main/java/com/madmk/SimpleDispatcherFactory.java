package com.madmk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author madmk
 * @date 2020/8/27 20:40
 * @description: 简单无参的调度器实例生成者
 */
public class SimpleDispatcherFactory implements DispatcherFactory {
    @Override
    public <T> T create(Class<T> aClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors=aClass.getConstructors();
        if(constructors.length == 0){
            throw new IllegalArgumentException("类："+aClass.getName()+",必须至少有一个公共无参构造函数");
        }
        for (Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount()==0){
                return (T)constructor.newInstance();
            }
        }
        throw new IllegalArgumentException("类："+aClass.getName()+",必须至少有一个公共无参构造函数");
    }
}
