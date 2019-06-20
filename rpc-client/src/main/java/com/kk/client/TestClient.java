package com.kk.client;

import com.kk.client.proxy.ProxyFactory;
import com.kk.rpc.api.biz.service.HelloService;

public class TestClient {

    public static void main(String[] args) {

        HelloService service = ProxyFactory.create(HelloService.class);
        String result = service.getResult("云");
        System.out.println("result:"+result);
        service.say();
    }
}
