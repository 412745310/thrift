package com.chelsea.thriftSpring.thrift;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * thrift 客户端调用服务端的地址列表提供者
 */
public interface ThriftClientServerAddressProvider {

	/**
	 * 获取所有地址.
	 * 
	 * @return 地址列表
	 */
	public List<InetSocketAddress> getAll();

	/**
	 * 选取地址.
	 * 
	 * @return 地址
	 */
	public InetSocketAddress selector();

	/**
	 * 关闭资源方法.
	 */
	public void close();
}
