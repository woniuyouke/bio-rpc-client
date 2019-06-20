package com.kk.client.proxy;


import java.lang.reflect.Proxy;

public class ProxyFactory {
    public static <T> T create(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(),new Class[]{tClass},new ProxyHandle(tClass));
    }
}
