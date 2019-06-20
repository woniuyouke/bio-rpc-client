package com.kk.client.proxy;

import com.kk.rpc.api.annotation.KInvoker;
import com.kk.rpc.api.channel.KRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class ProxyHandle implements InvocationHandler {
    private Class clazz;
    public ProxyHandle(Class clazz){
        this.clazz = clazz;
    }
    public Object invoke(Object proxy, Method method, Object[] args)  {
        log.info("开始执行代理方法");
        log.info("开始封装krequest入参");
        KInvoker kInvoker = (KInvoker) clazz.getAnnotation(KInvoker.class);
        if(kInvoker == null){
            throw  new RuntimeException("调用接口不是rpc接口");
        }
        KRequest kRequest = new KRequest();
        kRequest.setClazzName(kInvoker.value());
        kRequest.setParams(args);
        kRequest.setMethodName(method.getName());
        Object object = handleSocket(kRequest);
        log.info("代理方法处理完毕");
        return object;
    }

    private Object handleSocket(KRequest kRequest) {
        Socket socket = null;
        Object o = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            log.info("建立连接");
            socket = new Socket();
            socket.connect(new InetSocketAddress(10010));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            log.info("发送信息={}",kRequest.getParams());
            objectOutputStream.writeObject(kRequest);
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            o = objectInputStream.readObject();
            log.info("接收到返回信息={}",o);
        }catch (Exception e){
            e.printStackTrace();
            log.error("",e);
        }finally {
            if(objectOutputStream != null){
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(objectInputStream != null){
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socket != null && !socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return o;
    }
}
