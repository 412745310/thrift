package com.chelsea.thrift.service.impl;

import org.apache.thrift.TException;

import com.chelsea.thrift.service.HelloWorldService;

public class HelloWorldServiceImpl implements HelloWorldService {
	
	public String sayHello(String username) throws TException {
		return "Hi," + username;
	}
}
