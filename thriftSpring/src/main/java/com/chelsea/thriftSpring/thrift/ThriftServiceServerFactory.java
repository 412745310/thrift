package com.chelsea.thriftSpring.thrift;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chelsea.thriftSpring.thrift.NormalServiceHandler;
import com.chelsea.thriftSpring.thrift.NormalService.Processor;

/**
 * rpc服务端
 * @author haow
 *
 */
public class ThriftServiceServerFactory {
	private static Logger LOGGER = LoggerFactory.getLogger(ThriftServiceServerFactory.class);

	// 端口号
	private Integer port;

	private NormalServiceHandler normalServiceHandler;// serice实现类

	// 服务启动线程
	private ServerThread serverThread;

	public void setNormalServiceHandler(NormalServiceHandler normalServiceHandler) {
		this.normalServiceHandler = normalServiceHandler;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * 启动方法
	 * @throws Exception 异常
	 */
	public void init() throws Exception {
		Class<?> serviceClass = this.normalServiceHandler.getClass();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class<?>[] interfaces = serviceClass.getInterfaces();
		if (interfaces.length == 0) {
			throw new IllegalClassFormatException("service-class should implements Iface");
		}

		Processor<?> processor = null;
		for (Class<?> clazz : interfaces) {
			String cname = clazz.getSimpleName();
			if (!cname.equals("Iface")) {
				continue;
			}
			String serviceName = clazz.getEnclosingClass().getName() + "$Processor";
			try {
				Class<?> pclass = classLoader.loadClass(serviceName);
				if (!pclass.isAssignableFrom(Processor.class)) {
					continue;
				}
				Constructor<?> constructor = pclass.getConstructor(clazz);
				processor = (Processor<?>) constructor.newInstance(this.normalServiceHandler);
				LOGGER.info("注册成功服务：{}, 协议名：{}", serviceName, processor.getClass().getSimpleName());
				break;
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.info("注册失败服务：{}, 类名:{}, 协议名：{}", serviceName, processor.getClass().getSimpleName());
			}
		}

		if (processor == null) {
			throw new IllegalClassFormatException("service-class should implements Iface");
		}

		// 需要单独的线程,因为serve方法是阻塞的.
		this.serverThread = new ServerThread(processor, this.port);
		this.serverThread.start();
	}

	/**
	 * 启动服务线程.
	 * @author haow
	 *
	 */
	class ServerThread extends Thread {
		private TServer server;

		/**
		 * 构造方法.
		 * @param processor 协议类
		 * @param port 端口
		 * @throws Exception 异常
		 */
		public ServerThread(Processor<?> processor, int port) throws Exception {
			TServerSocket serverTransport = new TServerSocket(port);
			Factory portFactory = new TBinaryProtocol.Factory(true, true);
			Args args = new Args(serverTransport);
			args.processor(processor);
			args.protocolFactory(portFactory);
			this.server = new TThreadPoolServer(args);
		}

		@Override
		public void run() {
			try {
				LOGGER.info("Starting server on port {} ", port);
				this.server.serve();
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("Starting server error, on port {} ", port);
			}
		}

		/**
		 * 关闭资源
		 */
		public void stopServer() {
			this.server.stop();
		}
	}

	/**
	 * 关闭资源
	 */
	public void close() {
		this.serverThread.stopServer();
	}
}