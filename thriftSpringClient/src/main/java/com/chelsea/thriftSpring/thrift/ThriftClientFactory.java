package com.chelsea.thriftSpring.thrift;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端工厂类
 */
public class ThriftClientFactory {

	private Logger LOGGER = LoggerFactory.getLogger(ThriftClientFactory.class);

	/**
	 * 访问服务端的地址提供对象
	 */
	private final ThriftClientServerAddressProvider addressProvider;

	/**
	 * 客户端工厂对象
	 */
	private final TServiceClientFactory<TServiceClient> clientFactory;

	/**
	 * 构造方法.
	 * 
	 * @param addressProvider
	 *            服务端地址列表提供对象
	 * @param clientFactory
	 *            客户端工厂对象
	 * @throws Exception
	 *             异常
	 */
	protected ThriftClientFactory(ThriftClientServerAddressProvider addressProvider,
			TServiceClientFactory<TServiceClient> clientFactory) throws Exception {
		this.addressProvider = addressProvider;
		this.clientFactory = clientFactory;
	}

	/**
	 * 创建客户端对象.
	 * 
	 * @return 客户端对象
	 * @throws Exception
	 *             创建异常
	 */
	public TServiceClient makeObject() throws Exception {
		TServiceClient client = null;
		InetSocketAddress address = null;
		try {
			address = addressProvider.selector();
			TSocket tsocket = new TSocket(address.getHostName(), address.getPort());
			TProtocol protocol = new TBinaryProtocol(tsocket);
			client = this.clientFactory.getClient(protocol);
			tsocket.open();
		} catch (Exception e) {
			LOGGER.error(null == address ? "获取地址失败" : address.getHostName() + ":" + address.getPort(), e);
			return null;
		}

		return client;
	}

	/**
	 * 校验客户端是否可用.
	 * 
	 * @param client
	 *            客户端对象
	 * @return true: 状态正常， 否则不可用
	 */
	public boolean validate(TServiceClient client) {
		if (null == client) {
			return false;
		}

		boolean socketIsOpen = false;
		String hosts = null;
		try {
			TTransport pin = client.getInputProtocol().getTransport();
			if (null != pin && pin instanceof TSocket) {
				Socket socket = ((TSocket) pin).getSocket();
				socketIsOpen = socketIsClose(socket);
				if (null != socket) {
					InetAddress address = socket.getLocalAddress();
					if (null != address) {
						hosts = address.getHostName() + ":" + socket.getPort();
					}
				}
			}
			boolean status = socketIsOpen && pin.isOpen();
			return status;
		} catch (Exception e) {
			LOGGER.error("链接服务端异常, 地址:" + hosts, e);
			return false;
		}
	}
	
	/**
	 * 检测socket是否可用
	 */
	public boolean socketIsClose(Socket socket) {
		try {
			socket.sendUrgentData(0xFF);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 销毁客户端
	 * 
	 * @param client
	 *            客户端对象.
	 */
	public void destroy(TServiceClient client) {
		if (null == client) {
			return;
		}
		try {
			TTransport pin = client.getInputProtocol().getTransport();
			pin.close();
			client = null;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
