package com.chelsea.thrift;

import junit.framework.TestCase;

import org.junit.Test;

import com.chelsea.thrift.server.HelloServiceServer;

public class TestThriftServer extends TestCase {

	@Test
	public void testStartServer() {
		HelloServiceServer server = new HelloServiceServer();
		server.startServer();
	}

}
