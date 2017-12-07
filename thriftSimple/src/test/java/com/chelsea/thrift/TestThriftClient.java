package com.chelsea.thrift;

import junit.framework.TestCase;

import org.junit.Test;

import com.chelsea.thrift.client.HelloClient;

public class TestThriftClient extends TestCase {

	@Test
	public void testThriftClient() {
		HelloClient client = new HelloClient();
		client.startClient("张三");
	}
}
