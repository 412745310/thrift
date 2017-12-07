package com.chelsea.thriftSpring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.chelsea.thriftSpring.domain.RspMessage;
import com.chelsea.thriftSpring.service.HelloWorldService;

public class ClientTest {

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/spring-thrift-client.xml");
		HelloWorldService service = (HelloWorldService)ctx.getBean("helloWorldServiceImpl");
		String result = service.sayHello("张三");
		RspMessage rsp = JSON.parseObject(result, RspMessage.class);
		System.out.println(rsp.getObj().toString());
	}

}
