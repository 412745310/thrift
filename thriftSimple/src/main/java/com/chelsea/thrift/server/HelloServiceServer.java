package com.chelsea.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;

import com.chelsea.thrift.iface.HelloWorldServiceIface;
import com.chelsea.thrift.service.impl.HelloWorldServiceImpl;

public class HelloServiceServer {

	public static final int SERVER_PORT = 7090;

	public void startServer() {
		try {
			System.out.println("HelloWorld TSimpleServer start ....");
			TProcessor tprocessor = new HelloWorldServiceIface.Processor<HelloWorldServiceIface.Iface>(
					new HelloWorldServiceImpl());
			// 简单的单线程服务模型，一般用于测试
			TServerSocket serverTransport = new TServerSocket(SERVER_PORT);
			TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
			//TServer.Args tArgs = new TServer.Args(serverTransport);
			tArgs.processor(tprocessor);
			tArgs.protocolFactory(new TBinaryProtocol.Factory());
			TServer server = new TThreadPoolServer(tArgs);
			// TServer server = new TSimpleServer(tArgs);
			server.serve();

		} catch (Exception e) {
			System.out.println("Server start error!!!");
			e.printStackTrace();
		}
	}
}
