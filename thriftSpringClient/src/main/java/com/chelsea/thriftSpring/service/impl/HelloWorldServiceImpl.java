package com.chelsea.thriftSpring.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.chelsea.thriftSpring.service.HelloWorldService;
import com.chelsea.thriftSpring.thrift.NormalServiceClient;

@Service
public class HelloWorldServiceImpl implements HelloWorldService {
	
	@Autowired
    NormalServiceClient normalServiceClient;

	public String sayHello(String username) {
		String json = JSON.toJSONString(username);
		String content = "";
		try {
            content = normalServiceClient.getResponse("helloWorldService", "sayHello", json);
        }catch (Exception e)   {
            System.out.println("查询结果出错：" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
		return content;
	}
	
}
