package com.chelsea.thriftSpring.service.impl;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.chelsea.thriftSpring.domain.RspMessage;
import com.chelsea.thriftSpring.service.HelloWorldService;

@Service
public class HelloWorldServiceImpl implements HelloWorldService {

	public String sayHello(String username) {
		RspMessage rsp = RspMessage.getSuccessMsg();
		String result = "Hi," + username;
		rsp.setObj(result);
		return JSONObject.toJSONString(rsp);
	}
}
